package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;


import com.github.kousaba.mekanism_beyond.beyond_energy.BeyondEnergyUnit;
import com.github.kousaba.mekanism_beyond.beyond_energy.BeyondVariableCapacityEnergyContainer;
import com.github.kousaba.mekanism_beyond.beyond_energy.IBeyondEnergyContainer;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondChemicals;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class PBFusionReactorMultiblockData extends MultiblockData {
    public static final int MAX_INJECTION = 200;
    public static final double IGNITION_TEMPERATURE = 1_000_000_000.0;
    @ContainerSync
    public IChemicalTank protonTank;
    @ContainerSync
    public IChemicalTank boronTank;
    @ContainerSync
    public double visualRadius = 5.0;
    @ContainerSync
    public IBeyondEnergyContainer energyContainer;
    @ContainerSync
    public double lastGenerationRateBE = 0;
    public List<BlockPos> superchargedCoils = new ArrayList<>();
    @ContainerSync
    private double plasmaTemperature = 0;
    @ContainerSync
    private boolean burning = false; // privateにしてGetter/Setterを追加
    @ContainerSync
    private int injectionRate = 2;
    private double internalPulseTime = 0;

    public PBFusionReactorMultiblockData(TileEntityMekanism tile) {
        super(tile);
        chemicalTanks.add(protonTank = VariableCapacityChemicalTank.input(this, () -> 200_000L,
                chem -> chem.is(MekBeyondChemicals.PROTON.get()), this));
        chemicalTanks.add(boronTank = VariableCapacityChemicalTank.input(this, () -> 200_000L,
                chem -> chem.is(MekBeyondChemicals.BORON11.get()), this));

        this.energyContainer = BeyondVariableCapacityEnergyContainer.output(() -> 10_000.0, this);
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (world.isClientSide) return needsPacket;

        if (!isBurning()) {
            // 燃料があり、且つ温度が10億度を超えていたら点火
            if (plasmaTemperature >= IGNITION_TEMPERATURE &&
                    protonTank.getStored() > 0 && boronTank.getStored() > 0) {
                setBurning(true);
                needsPacket = true;
            }
        }

        if (burning) {
            long burnAmount = (long) injectionRate / 2;
            if (protonTank.getStored() >= burnAmount && boronTank.getStored() >= burnAmount && burnAmount > 0) {
                protonTank.shrinkStack(burnAmount, Action.EXECUTE);
                boronTank.shrinkStack(burnAmount, Action.EXECUTE);

                // 発電量計算 (BE/t)
                double generatedBE = (double) injectionRate * 0.5;

                // IEnergyContainer#insert は long (Joules) を受け取るため、変換して挿入
                energyContainer.insert(BeyondEnergyUnit.toJoules(generatedBE), Action.EXECUTE, AutomationType.INTERNAL);
                this.lastGenerationRateBE = generatedBE;
            } else {
                burning = false;
                this.lastGenerationRateBE = 0;
                needsPacket = true;
            }
            // 速度計算
            float speedFactor = (float) Math.sqrt(getInjectionRate() / 200.0f);
            internalPulseTime += Math.max(0.01f, speedFactor); // 停止しないように最小値を設定

            double cycleDuration = 60.0;
            double progress = (internalPulseTime % cycleDuration) / cycleDuration;

            // サーバー側で計算
            this.visualRadius = 5.0 * (1.0 - progress);

            // 常に同期が必要なため、燃焼中は毎tickパケットを飛ばす設定
            needsPacket = true;
        } else {
            this.lastGenerationRateBE = 0;
            if (plasmaTemperature > 0) {
                plasmaTemperature = Math.max(0, plasmaTemperature * 0.95);
            }
        }

        energyContainer.updateLastUsage();
        return needsPacket;
    }

    public boolean isBurning() {
        return burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    public int getInjectionRate() {
        return injectionRate;
    }

    public void setInjectionRate(int rate) {
        this.injectionRate = Math.max(0, Math.min(rate, MAX_INJECTION));
        markDirty();
    }

    public double getVisualRadius() {
        return visualRadius;
    }

    public void addTemperatureFromEnergyInput(double energy) {
        // Mekanismのレーザーエネルギーを温度に変換（係数は調整してください）
        this.plasmaTemperature += energy / 10.0;
        markDirty();
    }

    public double getPlasmaTemp() {
        return plasmaTemperature;
    }

    @Override
    public void writeUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeUpdateTag(tag, provider);
        tag.putBoolean("burning", isBurning());
        tag.putDouble("radius", visualRadius);
        // コイル座標の同期
        if (superchargedCoils != null) {
            long[] posArray = new long[superchargedCoils.size()];
            for (int i = 0; i < superchargedCoils.size(); i++) {
                posArray[i] = superchargedCoils.get(i).asLong();
            }
            tag.putLongArray("coils", posArray);
        }
    }

    @Override
    public void readUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.readUpdateTag(tag, provider);
        this.setBurning(tag.getBoolean("burning"));
        this.visualRadius = tag.getDouble("radius");
        this.superchargedCoils.clear();
        if (tag.contains("coils")) {
            for (long p : tag.getLongArray("coils")) {
                this.superchargedCoils.add(BlockPos.of(p));
            }
        }
    }


}