package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.registration.ModChemicals;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class TransmuterMultiblockData extends MultiblockData {
    public static final int MIN_X = 5;
    public static final int MIN_Z = 5;
    public static final int MIN_Y = 7;
    public static final int MAX_X = 17;
    public static final int MAX_Z = 17;
    public static final int MAX_Y = 18;

    public static final long BASE_ENERGY_CAPACITY = 100_000_000_000L;
    public static final int BASE_WATER_CAPACITY = 100_000_000;
    public static final long BASE_CHEMICAL_CAPACITY = 100_000_000L;

    public static final long ENERGY_USAGE_PER_TICK = 10_000_000;
    public static final int WATER_USAGE_PER_TICK = 10_000;

    public static final double BASE_PROBABILITY = 0.001;
    public static final double SIZE_PROBABILITY_FACTOR = 0.0001;
    public static final long BASE_PRODUCTION_AMOUNT = 100;
    public static final double COIL_SPEED_BONUS = 0.5;

    @ContainerSync
    public IEnergyContainer energyContainer;
    @ContainerSync
    public IExtendedFluidTank waterTank;
    @ContainerSync
    public IChemicalTank uraniumWaterTank;

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

    private final List<CapabilityOutputTarget<IChemicalHandler>> chemicalOutputTargets = new ArrayList<>();

    public TransmuterMultiblockData(TileEntityMekanism tile) {
        super(tile);
        fluidTanks.add(waterTank = VariableCapacityFluidTank.input(this, () -> BASE_WATER_CAPACITY,
                fluid -> fluid.is(FluidTags.WATER), this));
        chemicalTanks.add(uraniumWaterTank = VariableCapacityChemicalTank.input(this, () -> BASE_CHEMICAL_CAPACITY,
                chemical -> chemical.is(ModChemicals.URANIUM_WATER.get()), this));
        energyContainers.add(energyContainer = VariableCapacityEnergyContainer.input(BASE_ENERGY_CAPACITY, this));
    }

    public TransmuterMultiblockData() {
        super(null);
        System.out.println("transmutermultiblockdata");

        fluidTanks.add(waterTank = VariableCapacityFluidTank.input(this, () -> BASE_WATER_CAPACITY,
                fluid -> fluid.is(FluidTags.WATER), this));
        chemicalTanks.add(uraniumWaterTank = VariableCapacityChemicalTank.input(this, () -> BASE_CHEMICAL_CAPACITY,
                chemical -> chemical.is(ModChemicals.URANIUM_WATER.get()), this));
        energyContainers.add(energyContainer = VariableCapacityEnergyContainer.input(BASE_ENERGY_CAPACITY, this));
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        scanInternalStructures(world);
        int area = length() * width();
        currentProbability = BASE_PROBABILITY + (area * SIZE_PROBABILITY_FACTOR);
        currentProductionRate = (long) (BASE_PRODUCTION_AMOUNT * (1 + electromagneticCoilCount * COIL_SPEED_BONUS));
    }

    private void scanInternalStructures(Level world) {
        electromagneticCoilCount = 0;
        hasSuperchargedCoil = false;
        boolean airGapValid = true;

        // 内部の高さ範囲
        int minH = getMinPos().getY() + 1;
        int maxH = getMaxPos().getY() - 1;

        // 中央の計算 (空気層3層の中心)
        int midY = (minH + maxH) / 2;

        // 中心座標 (X, Z)
        int centerX = (getMinPos().getX() + getMaxPos().getX()) / 2;
        int centerZ = (getMinPos().getZ() + getMaxPos().getZ()) / 2;

        for (int y = minH; y <= maxH; y++) {
            for (int x = getMinPos().getX() + 1; x <= getMaxPos().getX() - 1; x++) {
                for (int z = getMinPos().getZ() + 1; z <= getMaxPos().getZ() - 1; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    // --- 1. 中央の空気層3層 (midY-1, midY, midY+1) の判定 ---
                    if (y >= midY - 1 && y <= midY + 1) {
                        if (!state.isAir()) {
                            airGapValid = false; // 空気が必須の場所にブロックがあった
                        }
                        continue; // 空気層の処理はここまで
                    }

                    // --- 2. 上下コイル層の判定 ---
                    // 中心軸 (X, Zの中心)
                    if (x == centerX && z == centerZ) {
                        if (state.is(MekanismBlocks.SUPERCHARGED_COIL)) {
                            hasSuperchargedCoil = true; // 1つでもあればOK (上下に必要なら条件を追加可能)
                        }
                    }
                    // 中心軸の周囲 3x3
                    else if (Math.abs(x - centerX) <= 1 && Math.abs(z - centerZ) <= 1) {
                        if (state.is(GeneratorsBlocks.ELECTROMAGNETIC_COIL)) {
                            electromagneticCoilCount++;
                        }
                    }
                }
            }
        }

        // 最終的な有効性判定: 空気層が正しく、かつ Supercharged Coil が存在すること
        this.active = airGapValid && hasSuperchargedCoil;
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);

        // 動作条件: Supercharged Coilが存在し、水と電力が足りていること
        if (hasSuperchargedCoil && energyContainer.getEnergy() >= ENERGY_USAGE_PER_TICK && waterTank.getFluidAmount() >= WATER_USAGE_PER_TICK) {
            active = true;

            // エネルギーと水の消費
            energyContainer.extract(ENERGY_USAGE_PER_TICK, Action.EXECUTE, AutomationType.INTERNAL);
            waterTank.shrinkStack(WATER_USAGE_PER_TICK, Action.EXECUTE);

            // 確率判定
            if (world.random.nextDouble() < currentProbability) {
                // 生成処理
                uraniumWaterTank.insert(new ChemicalStack(ModChemicals.URANIUM_WATER.get(), currentProductionRate), Action.EXECUTE, AutomationType.INTERNAL);
            }
        } else {
            active = false;
        }

        // クライアント同期が必要な状態変化があれば packet を送る
        if (world.getGameTime() % 20 == 0) {
            needsPacket = true;
        }

        if (!chemicalOutputTargets.isEmpty() && !uraniumWaterTank.isEmpty()) {
            ChemicalUtil.emit(getActiveOutputs(chemicalOutputTargets), uraniumWaterTank);
        }

        return needsPacket;
    }

    @Override
    protected void updateEjectors(Level world) {
        chemicalOutputTargets.clear();
        for (IValveHandler.ValveData valve : valves) {
            TileEntityTransmuterPort tile = WorldUtils.getTileEntity(TileEntityTransmuterPort.class, world, valve.location);
            if (tile != null) {
                tile.addGasTargetCapability(chemicalOutputTargets, valve.side);
            }
        }
    }

    public void setCoilData(boolean hasSuper, int count) {
        this.hasSuperchargedCoil = hasSuper;
        this.electromagneticCoilCount = count;
        int area = length() * width();
        currentProbability = BASE_PROBABILITY + (area * SIZE_PROBABILITY_FACTOR);
        currentProductionRate = (long) (BASE_PRODUCTION_AMOUNT * (1 + electromagneticCoilCount * COIL_SPEED_BONUS));
    }

    public boolean hasSuperchargedCoil() {
        return this.hasSuperchargedCoil;
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
