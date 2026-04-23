package com.github.kousaba.mekanism_beyond.beyond_energy;

import mekanism.api.energy.IEnergyContainer;

public interface IBeyondEnergyContainer extends IEnergyContainer {
    double getEnergyBE();

    void setEnergyBE(double be);

    boolean useEnergyBE(double be);

    double getMaxEnergyBE();

    long getEnergyFE();

    double getLastUsageBE();

    void setLastUsageBE(double usage);

    void updateLastUsage();
}
