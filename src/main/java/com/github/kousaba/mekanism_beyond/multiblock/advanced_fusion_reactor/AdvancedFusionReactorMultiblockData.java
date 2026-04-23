package com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondChemicals;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.HeatUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.registries.GeneratorsChemicals;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class AdvancedFusionReactorMultiblockData extends MultiblockData {
    public static final int MAX_INJECTION = 200;
    private static final double BURN_TEMPRATURE = 100_000_000;

    private static final double HEAT_PER_INJECTION = 10_000_000;
    private static final long STEAM_PER_WATER = 1;
    private static final long NEUTRO_PER_DUTERIUM = 1;
    private final List<CapabilityOutputTarget<IChemicalHandler>> chemicalOutputTargets = new ArrayList<>();
    @ContainerSync
    public IChemicalTank deuteriumTank;
    @ContainerSync
    public IChemicalTank neutronTank;
    @ContainerSync
    public IExtendedFluidTank waterTank;
    @ContainerSync
    public IChemicalTank steamTank;
    @ContainerSync
    public IHeatCapacitor heatCapacitor;
    @ContainerSync
    private double plasmaTemperature = 0;
    @ContainerSync
    private double casingTemperature = 0;
    @ContainerSync
    private int injectionRate = 2;
    @ContainerSync
    private boolean burning = false;

    public AdvancedFusionReactorMultiblockData(TileEntityMekanism tile) {
        super(tile);
        chemicalTanks.add(deuteriumTank = VariableCapacityChemicalTank.input(this, () -> 100_000L,
                chem -> chem.is(GeneratorsChemicals.DEUTERIUM.get()), this));
        chemicalTanks.add(neutronTank = VariableCapacityChemicalTank.output(this, () -> 1_000_000L,
                chem -> chem.is(MekBeyondChemicals.NEUTRON.get()), this));
        fluidTanks.add(waterTank = VariableCapacityFluidTank.input(this, () -> 100_000_000,
                fluid -> fluid.is(FluidTags.WATER), this));
        chemicalTanks.add(steamTank = VariableCapacityChemicalTank.output(this, () -> 100_000_000L,
                chem -> chem.is(MekanismChemicals.STEAM.get()), this));
        heatCapacitors.add(heatCapacitor = VariableHeatCapacitor.create(
                1000.0,
                () -> 1.0,
                () -> 1.0,
                () -> 300.0,
                this
        ));
    }

    @Override
    public boolean tick(Level world) {
        // 1. スーパークラスの処理（バルブの更新など）
        boolean needsPacket = super.tick(world);


        if (world.isClientSide) {
            return needsPacket;
        }

        // 2. ヒートキャパシターから現在の外殻温度を取得して同期
        this.casingTemperature = heatCapacitor.getTemperature();

        boolean wasBurning = this.burning;

        // 3. 点火・燃焼判定
        // 燃焼中でない場合、プラズマ温度が1億度(100MK)を超えており、且つ重水素があれば自動点火
        if (!burning) {
            if (plasmaTemperature >= 100_000_000 && !deuteriumTank.isEmpty()) {
                burning = true;
            }
        }

        // 4. 燃焼処理
        if (burning) {
            // 1tickあたりの重水素注入量（ユーザー設定の injectionRate）
            long amountToBurn = injectionRate;

            if (deuteriumTank.getStored() >= amountToBurn && amountToBurn > 0) {
                // 重水素を消費
                deuteriumTank.shrinkStack(amountToBurn, Action.EXECUTE);

                // 熱の発生: Mekanismの約3倍の熱効率（1レートあたり30,000,000ケルビン相当の熱量を加算）
                // ※プラズマ熱容量を100と仮定した場合の計算
                double energyReleased = amountToBurn * 30_000_000.0;
                plasmaTemperature += energyReleased / 100.0;

                // 中性子の生産: 重水素1mBにつき10mBの中性子を生成
                long neutronProduction = amountToBurn * 10;
                neutronTank.insert(MekBeyondChemicals.NEUTRON.asStack(neutronProduction), Action.EXECUTE, AutomationType.INTERNAL);
            } else {
                // 燃料が足りなければ燃焼停止
                burning = false;
            }
        }

        // 5. 熱移動 (プラズマ温度 -> 外殻温度)
        // プラズマから外殻へ熱が逃げる（伝導率 10%）
        double transferToCasing = (plasmaTemperature - casingTemperature) * 0.1;
        if (transferToCasing > 0) {
            plasmaTemperature -= transferToCasing;
            // 外殻（ヒートキャパシター）へ熱を注入
            heatCapacitor.handleHeat(transferToCasing * 100);
        }

        // 6. 蒸気生成ロジック (外殻温度 + 水 -> 蒸気)
        // 外殻温度が 373.15K (100度) を超えている場合のみ実行
        if (casingTemperature > 373.15) {
            // 水1mBを蒸気1mBにするのに必要なエネルギー（Mekanismの標準定数を使用）
            double enthalpy = HeatUtils.getWaterThermalEnthalpy();

            // 外殻が持っている「沸騰に回せる余剰熱量」
            // 外殻温度が100度以下にならない範囲で計算
            double availableHeat = (casingTemperature - 373.15) * heatCapacitor.getHeatCapacity();

            // 生成可能な最大蒸気量 (mB) = 利用可能な熱 ÷ 1mBあたりの必要熱
            long amountToVaporize = (long) (availableHeat / enthalpy);

            // 水の在庫量と、蒸気タンクの空き容量でクランプ（制限）
            amountToVaporize = Math.min(amountToVaporize, (long) waterTank.getFluidAmount());
            amountToVaporize = Math.min(amountToVaporize, steamTank.getNeeded());

            if (amountToVaporize > 0) {
                // 水(Fluid)を消費
                waterTank.shrinkStack((int) amountToVaporize, Action.EXECUTE);

                // 蒸気(Chemical)を生成
                steamTank.insert(MekanismChemicals.STEAM.asStack(amountToVaporize), Action.EXECUTE, AutomationType.INTERNAL);

                // 消費したエネルギー分、外殻から熱を奪う
                heatCapacitor.handleHeat(-amountToVaporize * enthalpy);
            }
        }

        // 7. 自然放熱 (環境への熱損失)
        // プラズマと外殻は常に少しずつ冷えていく (室温300Kが下限)
        if (plasmaTemperature > 300) {
            plasmaTemperature *= 0.99; // 毎tick 1% 消失
        }
        // 外殻はヒートキャパシターの標準シミュレーションに任せるため、ここでは下限クランプのみ
        this.casingTemperature = Math.max(heatCapacitor.getTemperature(), 300);

        // 8. 自動搬出処理 (隣接するパイプ・チューブへ押し出し)
        // 中性子(Neutron)と蒸気(Steam)を排出
        if (!chemicalOutputTargets.isEmpty()) {
            if (!neutronTank.isEmpty()) {
                ChemicalUtil.emit(getActiveOutputs(chemicalOutputTargets), neutronTank);
            }
            if (!steamTank.isEmpty()) {
                ChemicalUtil.emit(getActiveOutputs(chemicalOutputTargets), steamTank);
            }
        }

        // 9. 状態変化の同期チェック
        // 燃焼状態が変わった、あるいは20tick(1秒)経過ごとにデータを同期
        if (wasBurning != burning || world.getGameTime() % 20 == 0) {
            needsPacket = true;
        }

        return needsPacket;
    }

    public int getInjectionRate() {
        return injectionRate;
    }

    public void setInjectionRate(int rate) {
        this.injectionRate = Mth.clamp(rate, 0, MAX_INJECTION);
        markDirty();
    }

    public boolean isBurning() {
        return burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    public double getPlasmaTemp() {
        return plasmaTemperature;
    }

    public double getCaseTemp() {
        return casingTemperature;
    }

    @Override
    protected void updateEjectors(Level world) {
        chemicalOutputTargets.clear();
        for (IValveHandler.ValveData valve : valves) {
            TileEntityAdvancedFusionPort tile = WorldUtils.getTileEntity(TileEntityAdvancedFusionPort.class, world, valve.location);
            if (tile != null) {
                tile.addGasTargetCapability(chemicalOutputTargets, valve.side);
            }
        }
    }

    public void addTemperatureFromEnergyInput(long energyAdded) {
        this.plasmaTemperature += (double) energyAdded / 100.0;
        markDirty();
    }

    @Override
    public void readUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.readUpdateTag(tag, provider);
        this.burning = tag.getBoolean("burning");
    }

    @Override
    public void writeUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeUpdateTag(tag, provider);
        tag.putBoolean("burning", this.burning);
    }
}
