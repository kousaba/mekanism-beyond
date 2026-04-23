package com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor;

import net.minecraft.util.StringRepresentable;

public enum AdvancedFusionPortMode implements StringRepresentable {
    INPUT("input"),       // ケミカル入力 (重水素)
    COOLANT("coolant"),   // 液体入力 (水)
    NEUTRON("neutron"),   // ケミカル出力 (中性子)
    OUTPUT("output");     // 液体出力 (蒸気)

    private static final AdvancedFusionPortMode[] MODES = values();
    private final String name;

    AdvancedFusionPortMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    // 次のモードに切り替えるメソッド
    public AdvancedFusionPortMode getNext() {
        return MODES[(ordinal() + 1) % MODES.length];
    }
}
