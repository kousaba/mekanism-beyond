package com.github.kousaba.mekanism_beyond.beyond_energy;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

public class BeyondVariableCapacityEnergyContainer implements IBeyondEnergyContainer {
    private final DoubleSupplier maxEnergyBE;
    @Nullable
    private final IContentsListener listener;
    private final Predicate<AutomationType> canInsert;
    private final Predicate<AutomationType> canExtract;
    private double energyBE = 0;
    private double lastUsageBE = 0;
    private double currentTickUsage = 0;

    protected BeyondVariableCapacityEnergyContainer(DoubleSupplier maxEnergyBE, Predicate<AutomationType> canInsert, Predicate<AutomationType> canExtract, @Nullable IContentsListener listener) {
        this.maxEnergyBE = maxEnergyBE;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
        this.listener = listener;
    }

    public static BeyondVariableCapacityEnergyContainer create(DoubleSupplier maxEnergyBE, @Nullable IContentsListener listener) {
        return new BeyondVariableCapacityEnergyContainer(maxEnergyBE, type -> true, type -> true, listener);
    }

    public static BeyondVariableCapacityEnergyContainer input(DoubleSupplier maxEnergyBE, @Nullable IContentsListener listener) {
        return new BeyondVariableCapacityEnergyContainer(maxEnergyBE, type -> true, type -> type == AutomationType.INTERNAL, listener);
    }

    public static BeyondVariableCapacityEnergyContainer output(DoubleSupplier maxEnergyBE, @Nullable IContentsListener listener) {
        return new BeyondVariableCapacityEnergyContainer(maxEnergyBE, type -> type == AutomationType.INTERNAL, type -> true, listener);
    }

    @Override
    public double getEnergyBE() {
        return energyBE;
    }

    @Override
    public void setEnergyBE(double be) {
        this.energyBE = Math.max(0, Math.min(be, getMaxEnergyBE()));
        onContentsChanged();
    }

    @Override
    public double getMaxEnergyBE() {
        return maxEnergyBE.getAsDouble();
    }

    @Override
    public long getEnergyFE() {
        return BeyondEnergyUnit.toFE(energyBE);
    }

    // --- IEnergyContainer 互換メソッド (long Joules) ---

    @Override
    public long getEnergy() {
        return BeyondEnergyUnit.toJoules(energyBE);
    }

    @Override
    public void setEnergy(long joules) {
        setEnergyBE(BeyondEnergyUnit.toBE(joules));
    }

    @Override
    public long getMaxEnergy() {
        return BeyondEnergyUnit.toJoules(getMaxEnergyBE());
    }

    @Override
    public long insert(long amount, Action action, AutomationType automationType) {
        // 自動化設定で搬入が許可されていない場合は拒否
        if (!canInsert.test(automationType)) return 0;

        if (amount <= 0) return 0;
        double inputBE = BeyondEnergyUnit.toBE(amount);
        double maxBE = getMaxEnergyBE();
        double toAddBE = Math.min(inputBE, maxBE - energyBE);

        if (toAddBE <= 0) return 0;

        if (action.execute()) {
            energyBE += toAddBE;
            onContentsChanged();
        }
        return BeyondEnergyUnit.toJoules(toAddBE);
    }

    @Override
    public long extract(long amount, Action action, AutomationType automationType) {
        if (!canExtract.test(automationType)) return 0;
        if (amount <= 0) return 0;

        double outputBE = BeyondEnergyUnit.toBE(amount);
        double toExtractBE = Math.min(energyBE, outputBE);
        if (toExtractBE <= 0) return 0;

        if (action.execute()) {
            energyBE -= toExtractBE;
            if (automationType == AutomationType.INTERNAL) {
                // 計算用のバッファに加算
                this.currentTickUsage += toExtractBE;
            }
            onContentsChanged();
        }
        return BeyondEnergyUnit.toJoules(toExtractBE);
    }

    @Override
    public boolean useEnergyBE(double be) {
        // 完全に足りている場合のみ消費
        if (energyBE < be) return false;
        energyBE -= be;
        // 計算用のバッファに加算
        this.currentTickUsage += be;
        onContentsChanged();
        return true;
    }

    @Override
    public void onContentsChanged() {
        if (listener != null) listener.onContentsChanged();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("energyBE", energyBE);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        // ロード時に現在の最大容量でクランプ
        this.energyBE = Math.min(nbt.getDouble("energyBE"), getMaxEnergyBE());
    }

    @Override
    public double getLastUsageBE() {
        return lastUsageBE;
    }

    @Override
    public void setLastUsageBE(double usage) {
        this.lastUsageBE = usage;
    }

    @Override
    public void updateLastUsage() {
        this.lastUsageBE = currentTickUsage;
        this.currentTickUsage = 0;
    }
}
