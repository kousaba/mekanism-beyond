package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MekBeyondItemModelProvider extends ItemModelProvider {
    public MekBeyondItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(MekanismBeyond.NANO_ALLOY.get());
        withExistingParent("transmuter_casing", modLoc("block/transmuter_casing"));
        withExistingParent("transmuter_port", modLoc("block/transmuter_port"));
        withExistingParent("advanced_fusion_casing", modLoc("block/advanced_fusion_casing"));
        withExistingParent("advanced_fusion_port", modLoc("block/advanced_fusion_port"));
        withExistingParent("magnetic_stabilization_coil", modLoc("block/magnetic_stabilization_coil"));
        withExistingParent("beyond_fusion_casing", modLoc("block/beyond_fusion_casing"));
        withExistingParent("beyond_fusion_port", modLoc("block/beyond_fusion_port"));
    }
}
