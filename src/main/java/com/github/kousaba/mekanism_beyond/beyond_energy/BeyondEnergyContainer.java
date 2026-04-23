package com.github.kousaba.mekanism_beyond.beyond_energy;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class BeyondEnergyContainer implements IBeyondEnergyContainer {
    @Nullable
    private final IContentsListener listener;
    private double energyBE = 0;
    private double lastUsageBE = 0;
    private double maxEnergyBE;
    private double currentTickUsage = 0;

    public BeyondEnergyContainer(double maxEnergyBE, @Nullable IContentsListener listener) {
        this.maxEnergyBE = maxEnergyBE;
        this.listener = listener;
    }

    @Override
    public double getEnergyBE() {
        return energyBE;
    }

    @Override
    public void setEnergyBE(double be) {
        this.energyBE = Math.max(0, Math.min(be, maxEnergyBE));
        onContentsChanged();
    }

    @Override
    public double getMaxEnergyBE() {
        return maxEnergyBE;
    }

    @Override
    public long getEnergyFE() {
        return BeyondEnergyUnit.toFE(energyBE);
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
    public long getEnergy() {
        return BeyondEnergyUnit.toJoules(energyBE);
    }

    @Override
    public void setEnergy(long joules) {
        setEnergyBE(BeyondEnergyUnit.toBE(joules));
    }

    @Override
    public long getMaxEnergy() {
        return BeyondEnergyUnit.toJoules(maxEnergyBE);
    }

    @Override
    public long insert(long amount, Action action, AutomationType automationType) {
        if (amount <= 0) return 0;
        double inputBE = BeyondEnergyUnit.toBE(amount);
        double neededBE = maxEnergyBE - energyBE;
        double toAddBE = Math.min(inputBE, neededBE);

        if (action.execute()) {
            energyBE += toAddBE;
            onContentsChanged();
        }
        return BeyondEnergyUnit.toJoules(toAddBE);
    }

    @Override
    public long extract(long amount, Action action, AutomationType automationType) {
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
    public void onContentsChanged() {
        if (listener != null) listener.onContentsChanged();
    }

    // 保存処理 (NBT)
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("energyBE", energyBE);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.energyBE = nbt.getDouble("energyBE");
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
    public void updateLastUsage() {
        // ティックの最後で呼び出し：今ティックの合計を表示用に移し、蓄積をリセットする
        this.lastUsageBE = currentTickUsage;
        this.currentTickUsage = 0;
    }

}
