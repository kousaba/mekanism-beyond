package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import com.github.kousaba.mekanism_beyond.beyond_energy.BeyondVariableCapacityEnergyContainer;
import com.github.kousaba.mekanism_beyond.beyond_energy.IBeyondEnergyContainer;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondChemicals;
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
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    public static final double BASE_ENERGY_CAPACITY = 10.0;
    public static final int BASE_WATER_CAPACITY = 100_000_000;
    public static final long BASE_CHEMICAL_CAPACITY = 100_000_000L;

    public static final long ENERGY_USAGE_PER_TICK = 10_000_000;
    public static final int WATER_USAGE_PER_TICK = 10;

    public static final double BASE_PROBABILITY = 0.001;
    public static final double SIZE_PROBABILITY_FACTOR = 0.0001;
    public static final long BASE_PRODUCTION_AMOUNT = 100;
    public static final double COIL_SPEED_BONUS = 0.5;

    @ContainerSync
    public IBeyondEnergyContainer energyContainer;
    @ContainerSync
    public IExtendedFluidTank waterTank;
    private final List<CapabilityOutputTarget<IFluidHandler>> fluidOutputTargets = new ArrayList<>();
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
    @ContainerSync
    public IExtendedFluidTank heavyWaterTank;
    @ContainerSync
    public long currentEnergyUsage = ENERGY_USAGE_PER_TICK;

    private final List<CapabilityOutputTarget<IChemicalHandler>> chemicalOutputTargets = new ArrayList<>();
    @ContainerSync
    public int currentWaterUsage = WATER_USAGE_PER_TICK;

    public List<BlockPos> superchargedCoils = new ArrayList<>();
    public float prevScale = 0; // 流体の高さ補間用

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

    private void updateCalculations() {
        int area = length() * width();
        currentProbability = BASE_PROBABILITY + (area * SIZE_PROBABILITY_FACTOR);
        double speedMultiplier = 1.0 + (electromagneticCoilCount * COIL_SPEED_BONUS);
        currentProductionRate = (long) (BASE_PRODUCTION_AMOUNT * speedMultiplier);
        currentEnergyUsage = (long) (ENERGY_USAGE_PER_TICK * speedMultiplier);
        currentWaterUsage = (int) (WATER_USAGE_PER_TICK * speedMultiplier);
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        updateCalculations();
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
        if (hasSuperchargedCoil && energyContainer.getEnergy() >= currentEnergyUsage && waterTank.getFluidAmount() >= currentWaterUsage) {
            active = true;

            // エネルギーと水の消費
            energyContainer.extract(currentEnergyUsage, Action.EXECUTE, AutomationType.INTERNAL);
            waterTank.shrinkStack(currentWaterUsage, Action.EXECUTE);

            double exactHeavyWater = currentWaterUsage / 6400.0;
            int heavyWaterProduced = (int) exactHeavyWater;
            if (world.random.nextDouble() < (exactHeavyWater - heavyWaterProduced)) {
                heavyWaterProduced++;
            }
            if (heavyWaterProduced > 0) {
                heavyWaterTank.insert(new FluidStack(MekanismFluids.HEAVY_WATER.get(), heavyWaterProduced), Action.EXECUTE, AutomationType.INTERNAL);
            }

            // 確率判定
            if (world.random.nextDouble() < currentProbability) {
                // 生成処理
                uraniumWaterTank.insert(new ChemicalStack(MekBeyondChemicals.URANIUM_WATER.get(), currentProductionRate), Action.EXECUTE, AutomationType.INTERNAL);
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
        if (!fluidOutputTargets.isEmpty() && !heavyWaterTank.isEmpty()) {
            FluidUtils.emit(getActiveOutputs(fluidOutputTargets), heavyWaterTank);
        }

        float targetScale = (float) waterTank.getFluidAmount() / BASE_WATER_CAPACITY;
        if (Math.abs(prevScale - targetScale) > 0.01) {
            prevScale = (prevScale * 9 + targetScale) / 10;
        } else {
            prevScale = targetScale;
        }

        boolean isActuallyActive = hasSuperchargedCoil && energyContainer.getEnergy() >= currentEnergyUsage && waterTank.getFluidAmount() >= currentWaterUsage;
        if (this.active != isActuallyActive) {
            this.active = isActuallyActive;
            needsPacket = true; // これでパケットが送られる
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
        updateCalculations();
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
