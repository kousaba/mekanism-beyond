package com.github.kousaba.mekanism_extended.datagen;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper){
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(MekanismExtended.NANO_ALLOY.get());
        withExistingParent("transmutation_casing", modLoc("block/transmuter_casing"));
        withExistingParent("transmutation_port", modLoc("block/transmuter_port"));
    }
}
