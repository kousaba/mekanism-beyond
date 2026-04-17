package com.github.kousaba.mekanism_beyond.beyond_energy;

import net.minecraft.network.chat.Component;

public class BeyondEnergyDisplay {
    private static final String[] UNITS = {"", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"};

    public static String format(double be){
        if(be < 1000) return String.format("%.2f BE", be);
        int unitIndex = 0;
        while (be >= 1000 && unitIndex < UNITS.length - 1){
            be /= 1000;
            unitIndex++;
        }
        return String.format("%.2f %sBE", be, UNITS[unitIndex]);
    }
    public static Component getComponent(double be) {
        return Component.literal(format(be));
    }
}
