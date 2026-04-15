package com.github.kousaba.mekanism_extended.datagen;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add(MekanismExtended.NANO_ALLOY.get(), "Nano Alloy");
    }
}
