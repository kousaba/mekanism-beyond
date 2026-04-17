package com.github.kousaba.mekanism_beyond.beyond_energy;

import mekanism.api.math.MathUtils;

public class BeyondEnergyUnit {
    public static final double FE_PER_BE = 1_000_000_000.0;
    public static final double J_PER_FE = 2.5;
    public static final double J_PER_BE = FE_PER_BE * J_PER_FE;

    public static double toBE(long joules){
        return (double) joules / J_PER_BE;
    }

    public static long toJoules(double be){
        double joules = be * J_PER_BE;
        return (long) Math.min(Long.MAX_VALUE, joules);
    }

    public static long toFE(double be){
        double fe = be * FE_PER_BE;
        return (long) Math.min(Long.MAX_VALUE, fe);
    }
}
