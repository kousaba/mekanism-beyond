package com.github.kousaba.mekanism_beyond.beyond_energy;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import mekanism.common.util.MekanismUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum BeyondEnergyUnit implements StringRepresentable {
    JOULES("J", "j"),
    FE("FE", "fe"),
    BE("BE", "beyond"),
    ;

    public static final BeyondEnergyUnit[] UNITS = values();
    public static final double FE_PER_BE = 1_000_000_000.0;
    public static final double J_PER_FE = 2.5;
    public static final double J_PER_BE = FE_PER_BE * J_PER_FE;
    private static BeyondEnergyUnit configured = BE;
    private final String name;
    private final String tabName;

    BeyondEnergyUnit(String name, String tabName) {
        this.name = name;
        this.tabName = tabName;
    }

    public static BeyondEnergyUnit getConfigured() {
        return configured;
    }

    public static void setConfigured(BeyondEnergyUnit unit) {
        configured = unit;
    }

    public static double toBE(long joules) {
        return (double) joules / J_PER_BE;
    }

    public static long toJoules(double be) {
        double joules = be * J_PER_BE;
        return (long) Math.min(Long.MAX_VALUE, joules);
    }

    public static long toFE(double be) {
        double fe = be * FE_PER_BE;
        return (long) Math.min(Long.MAX_VALUE, fe);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public BeyondEnergyUnit getNext() {
        return UNITS[(ordinal() + 1) % UNITS.length];
    }

    public BeyondEnergyUnit getPrevious() {
        return UNITS[(ordinal() + UNITS.length - 1) % UNITS.length];
    }

    public ResourceLocation getIcon() {
        if (this == BE) {
            return ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "textures/gui/tabs/energy_info_be.png");
        }
        return MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_TAB, "energy_info_" + tabName + ".png");
    }
}
