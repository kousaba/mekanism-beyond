package com.github.kousaba.mekanism_extended;

import mekanism.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum ModLang implements ILangEntry {
    TRANSMUTER_PORT_MODE("tooltip", "transmuter_port_mode");

    private final String key;

    ModLang(String type, String path) {
        this(Util.makeDescriptionId(type, ResourceLocation.fromNamespaceAndPath(MekanismExtended.MODID, path)));
    }

    ModLang(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String getTranslationKey() {
        return key;
    }
}
