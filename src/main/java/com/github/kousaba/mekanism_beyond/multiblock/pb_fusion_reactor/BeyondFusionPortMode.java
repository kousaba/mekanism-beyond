package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;


import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;

public enum BeyondFusionPortMode implements StringRepresentable {
    INPUT("input"),       // ケミカル入力 (Proton, Boron-11)
    OUTPUT("output");     // BE出力

    private static final BeyondFusionPortMode[] MODES = values();
    private final String name;

    BeyondFusionPortMode(String name) {
        this.name = name;
    }

    @MethodsReturnNonnullByDefault
    @Override
    public String getSerializedName() {
        return name;
    }

    public BeyondFusionPortMode getNext() {
        return MODES[(ordinal() + 1) % MODES.length];
    }
}