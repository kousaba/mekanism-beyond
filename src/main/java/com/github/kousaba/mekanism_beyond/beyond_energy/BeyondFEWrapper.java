package com.github.kousaba.mekanism_beyond.beyond_energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

public class BeyondFEWrapper implements IEnergyStorage {
    private final IBeyondEnergyContainer container;
    private final boolean isInput; // trueなら搬入専用、falseなら搬出専用

    public BeyondFEWrapper(IBeyondEnergyContainer container, boolean isInput) {
        this.container = container;
        this.isInput = isInput;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!isInput || maxReceive <= 0) return 0;

        // FE(int) を BE(double) に変換
        double receiveBE = (double) maxReceive / BeyondEnergyUnit.FE_PER_BE;
        double currentBE = container.getEnergyBE();
        double maxBE = container.getMaxEnergyBE();
        double toAddBE = Math.min(receiveBE, maxBE - currentBE);

        if (!simulate) {
            container.setEnergyBE(currentBE + toAddBE);
        }
        // 実際に搬入できた量を FE(int) に戻して返す
        return (int) (toAddBE * BeyondEnergyUnit.FE_PER_BE);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (isInput || maxExtract <= 0) return 0;

        double extractBE = (double) maxExtract / BeyondEnergyUnit.FE_PER_BE;
        double currentBE = container.getEnergyBE();
        double toExtractBE = Math.min(extractBE, currentBE);

        if (!simulate) {
            container.setEnergyBE(currentBE - toExtractBE);
        }
        // 実際に搬出できた量を FE(int) に戻して返す
        return (int) (toExtractBE * BeyondEnergyUnit.FE_PER_BE);
    }

    @Override
    public int getEnergyStored() {
        // intの限界値でクランプして返す
        return (int) Math.min(Integer.MAX_VALUE, container.getEnergyFE());
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, container.getMaxEnergyBE() * BeyondEnergyUnit.FE_PER_BE);
    }

    @Override
    public boolean canExtract() {
        return !isInput;
    }

    @Override
    public boolean canReceive() {
        return isInput;
    }
}
