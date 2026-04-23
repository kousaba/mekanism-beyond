package com.github.kousaba.mekanism_beyond;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum MekBeyondLang implements ILangEntry {
    TRANSMUTER_PORT_MODE("tooltip", "transmuter_port_mode"),
    TRANSMUTER("multi", "transmuter"),
    MAIN_TAB("gui", "tab.main"),
    HEAT_TAB("gui", "tab.heat"),
    FUEL_TAB("gui", "tab.fuel"),
    STATS_TAB("gui", "tab.stats");

    private final String key;

    MekBeyondLang(String type, String path) {
        this.key = Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, path));
    }

    MekBeyondLang(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String getTranslationKey() {
        return key;
    }
}
