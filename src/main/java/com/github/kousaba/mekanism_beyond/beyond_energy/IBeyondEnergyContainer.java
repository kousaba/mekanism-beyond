package com.github.kousaba.mekanism_beyond.beyond_energy;

import mekanism.api.energy.IEnergyContainer;
import org.jetbrains.annotations.Nullable;

public interface IBeyondEnergyContainer extends IEnergyContainer {
    double getEnergyBE();
    double getMaxEnergyBE();
    long getEnergyFE();
    void setEnergyBE(double be);
}
