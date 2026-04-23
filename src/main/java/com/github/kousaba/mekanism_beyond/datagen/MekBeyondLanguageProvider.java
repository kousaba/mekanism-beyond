package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MekBeyondLanguageProvider extends LanguageProvider {
    public MekBeyondLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add(MekanismBeyond.NANO_ALLOY.get(), "Nano Alloy");
        add("chat.mekanism_beyond.port_mode", "Port Mode: %s");
        add("chat.mekanism_beyond.input", "Input");
        add("chat.mekanism_beyond.output", "Output");
        add("mekanism_beyond.gui.energy_info", "Beyond Energy Info");
        add("mekanism_beyond.port_mode.input", "Port Mode: Input (Deuterium)");
        add("mekanism_beyond.port_mode.coolant", "Port Mode: Coolant (Water)");
        add("mekanism_beyond.port_mode.neutron", "Port Mode: Output (Neutron)");
        add("mekanism_beyond.port_mode.output", "Port Mode: Output (Steam)");
    }
}
