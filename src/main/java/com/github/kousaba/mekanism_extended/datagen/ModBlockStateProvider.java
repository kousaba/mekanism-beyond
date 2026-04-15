package com.github.kousaba.mekanism_extended.datagen;

import com.github.kousaba.mekanism_extended.registration.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.TRANSMUTER_CASING.get());
        simpleBlock(ModBlocks.TRANSMUTER_PORT.get());
    }
}
