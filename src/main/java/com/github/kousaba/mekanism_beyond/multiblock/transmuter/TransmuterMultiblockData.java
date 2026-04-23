package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import com.github.kousaba.mekanism_beyond.beyond_energy.BeyondVariableCapacityEnergyContainer;
import com.github.kousaba.mekanism_beyond.beyond_energy.IBeyondEnergyContainer;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondChemicals;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondRecipeTypes;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class TransmuterMultiblockData extends MultiblockData {
    public static final int MIN_X = 5;
    public static final int MIN_Z = 5;
    public static final int MIN_Y = 7;
    public static final int MAX_X = 17;
    public static final int MAX_Z = 17;
    public static final int MAX_Y = 18;
    public static final double BASE_ENERGY_CAPACITY = 0.1;
    public static final int BASE_WATER_CAPACITY = 100_000_000;
    public static final long BASE_CHEMICAL_CAPACITY = 100_000_000L;
    public static final long ENERGY_USAGE_PER_TICK = 10_000_000;
    public static final int WATER_USAGE_PER_TICK = 10_000;
    public static final double BASE_PROBABILITY = 0.001;
    public static final double SIZE_PROBABILITY_FACTOR = 0.0001;
    public static final long BASE_PRODUCTION_AMOUNT = 100;
    public static final double COIL_SPEED_BONUS = 0.5;
    private static final double MIN_VOLUME = MIN_X * MIN_Y * MIN_Z; // 175
    private static final double MAX_VOLUME = MAX_X * MAX_Y * MAX_Z; // 5202
    private static final double MIN_COILS = 0;
    private static final double MAX_COILS = 448;
    private final List<CapabilityOutputTarget<IFluidHandler>> fluidOutputTargets = new ArrayList<>();
    private final List<CapabilityOutputTarget<IChemicalHandler>> chemicalOutputTargets = new ArrayList<>();
    @ContainerSync
    public IBeyondEnergyContainer energyContainer;
    @ContainerSync
    public IExtendedFluidTank waterTank;
    @ContainerSync
    public IChemicalTank uraniumWaterTank;
    @ContainerSync
    public IExtendedFluidTank heavyWaterTank;
    @ContainerSync
    public long currentEnergyUsage = ENERGY_USAGE_PER_TICK;
    @ContainerSync
    public int currentWaterUsage = WATER_USAGE_PER_TICK;
    public List<BlockPos> superchargedCoils = new ArrayList<>();
    public float prevScale = 0; // 流体の高さ補間用
    @ContainerSync
    public double lastSpeedMultiplier = 1.0;
    @ContainerSync
    private int electromagneticCoilCount = 0;
    @ContainerSync
    private boolean hasSuperchargedCoil = false;
    @ContainerSync
    private double currentProbability = 0;
    @ContainerSync
    private long currentProductionRate = 0;
    @ContainerSync
    private boolean active = false;

    public TransmuterMultiblockData(TileEntityMekanism tile) {
        super(tile);
        fluidTanks.add(waterTank = VariableCapacityFluidTank.input(this, () -> BASE_WATER_CAPACITY,
                fluid -> fluid.getFluid() == net.minecraft.world.level.material.Fluids.WATER, this));
        fluidTanks.add(heavyWaterTank = VariableCapacityFluidTank.output(this, () -> BASE_WATER_CAPACITY,
                fluid -> fluid.getFluid() == MekanismFluids.HEAVY_WATER.get(), this));
        chemicalTanks.add(uraniumWaterTank = VariableCapacityChemicalTank.output(this, () -> BASE_CHEMICAL_CAPACITY,
                chemical -> chemical.is(MekBeyondChemicals.URANIUM_WATER.get()), this));
        this.energyContainer = BeyondVariableCapacityEnergyContainer.input(
                () -> (double) length() * width() * height() * BASE_ENERGY_CAPACITY,
                this
        );
    }

    @Override
    public boolean tick(Level world) {
        // 最初でのリセットは削除
        boolean needsPacket = super.tick(world);
        if (world.isClientSide) return needsPacket;

        var recipes = world.getRecipeManager().getAllRecipesFor(MekBeyondRecipeTypes.TRANSMUTING.get());
        var recipeHolder = recipes.stream()
                .filter(r -> r.value().inputFluid().test(waterTank.getFluid()))
                .findFirst();

        boolean wasActive = this.active;
        boolean nowActive = false;

        if (recipeHolder.isPresent()) {
            TransmuterRecipe recipe = recipeHolder.get().value();

            // レシピから計算値を更新
            updateCalculationFromRecipe(recipe);

            if (energyContainer.getEnergyBE() >= currentEnergyUsage &&
                    waterTank.getFluidAmount() >= currentWaterUsage) {

                nowActive = true;

                // エネルギーと水の消費（この内部で currentTickUsage が加算される）
                energyContainer.useEnergyBE(currentEnergyUsage);
                waterTank.shrinkStack(currentWaterUsage, Action.EXECUTE);

                if (world.random.nextDouble() < currentProbability) {
                    ChemicalStack scaledChemical = recipe.outputChemical().copyWithAmount(this.currentProductionRate);
                    int scaledFluidAmount = (int) (recipe.outputFluid().getAmount() * (currentProductionRate / (double) recipe.outputChemical().getAmount()));
                    FluidStack scaledFluid = new FluidStack(recipe.outputFluid().getFluid(), scaledFluidAmount);

                    uraniumWaterTank.insert(scaledChemical, Action.EXECUTE, AutomationType.INTERNAL);
                    heavyWaterTank.insert(scaledFluid, Action.EXECUTE, AutomationType.INTERNAL);
                }
            }
        }

        if (wasActive != nowActive) {
            this.active = nowActive;
            needsPacket = true;
        }

        // 搬出処理
        if (!chemicalOutputTargets.isEmpty() && !uraniumWaterTank.isEmpty()) {
            ChemicalUtil.emit(getActiveOutputs(chemicalOutputTargets), uraniumWaterTank);
        }
        if (!fluidOutputTargets.isEmpty() && !heavyWaterTank.isEmpty()) {
            FluidUtils.emit(getActiveOutputs(fluidOutputTargets), heavyWaterTank);
        }

        // 【最重要！】ティックの最後で、今ティックの使用量を確定させる
        double oldUsage = energyContainer.getLastUsageBE();
        energyContainer.updateLastUsage();

        // 値が変わっていたらパケットを飛ばして同期する
        if (oldUsage != energyContainer.getLastUsageBE()) {
            needsPacket = true;
        }

        return needsPacket;
    }

    @Override
    protected void updateEjectors(Level world) {
        chemicalOutputTargets.clear();
        fluidOutputTargets.clear();
        for (IValveHandler.ValveData valve : valves) {
            TileEntityTransmuterPort tile = WorldUtils.getTileEntity(TileEntityTransmuterPort.class, world, valve.location);
            if (tile != null) {
                tile.addGasTargetCapability(chemicalOutputTargets, valve.side);
                tile.addFluidTargetCapability(fluidOutputTargets, valve.side);
            }
        }
    }

    public void setCoilData(boolean hasSuper, int count) {
        this.hasSuperchargedCoil = hasSuper;
        this.electromagneticCoilCount = count;
    }

    @Override
    public void readUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.readUpdateTag(tag, provider);
        // クライアント側でデータを受け取る
        this.prevScale = tag.getFloat("prevScale");
        this.active = tag.getBoolean("active");

        this.superchargedCoils.clear();
        for (long posLong : tag.getLongArray("coils")) {
            this.superchargedCoils.add(BlockPos.of(posLong));
        }
    }

    @Override
    public void writeUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeUpdateTag(tag, provider);
        // サーバー側からデータを送る
        tag.putFloat("prevScale", this.prevScale);
        tag.putBoolean("active", this.active);

        List<Long> positions = new ArrayList<>();
        for (BlockPos pos : this.superchargedCoils) {
            positions.add(pos.asLong());
        }
        tag.putLongArray("coils", positions);
    }

    public BlockPos getInnerMinPos() {
        return getMinPos().offset(1, 1, 1);
    }

    public BlockPos getInnerMaxPos() {
        return getMaxPos().offset(-1, -1, -1);
    }

    public boolean isMaster(BlockPos pos) {
        return isFormed() && pos.equals(getMinPos());
    }

    public boolean hasSuperchargedCoil() {
        return this.hasSuperchargedCoil;
    }

    private void updateCalculationFromRecipe(TransmuterRecipe recipe) {
        double currentVolume = (double) length() * width() * height();
        double volumeRatio = Math.clamp((currentVolume - MIN_VOLUME) / (MAX_VOLUME - MIN_VOLUME), 0, 1);
        this.currentProbability = recipe.minProbability() + (recipe.maxProbability() - recipe.minProbability()) * volumeRatio;

        double currentCoils = (double) electromagneticCoilCount;
        double coilRatio = (currentCoils - MIN_COILS) / (MAX_COILS - MIN_COILS);

        // 【修正】volumeRatio になっていたのを coilRatio に直す
        coilRatio = Math.clamp(coilRatio, 0, 1);

        // レシピの最小・最大に基づいた速度倍率の計算
        double speedMultiplier = recipe.minSpeedMultiplier() + (recipe.maxSpeedMultiplier() - recipe.minSpeedMultiplier()) * coilRatio;

        // 変数に保存（これで同期対象になる）
        this.lastSpeedMultiplier = speedMultiplier;

        this.currentProductionRate = (long) (recipe.outputChemical().getAmount() * speedMultiplier);
        this.currentEnergyUsage = (long) (recipe.energyUsageBE() * speedMultiplier);
        this.currentWaterUsage = (int) (recipe.inputFluid().getRepresentations().get(0).getAmount() * speedMultiplier);
    }

    @Override
    public boolean allowsStructuralGuiAccess(TileEntityStructuralMultiblock multiblock) {
        return true;
    }

    // --- Computer Methods (Optional) ---
    @ComputerMethod
    public boolean isActive() {
        return active;
    }

    @ComputerMethod
    public double getProbability() {
        return currentProbability;
    }

    @ComputerMethod
    public long getProductionRate() {
        return currentProductionRate;
    }

    @ComputerMethod
    public int getCoilCount() {
        return electromagneticCoilCount;
    }
}
