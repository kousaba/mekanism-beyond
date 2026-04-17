package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(event.includeClient(), new MekBeyondBlockStateProvider(packOutput, MekanismBeyond.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new MekBeyondRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new MekBeyondLanguageProvider(packOutput, MekanismBeyond.MODID, "en_us"));
        generator.addProvider(event.includeClient(), new MekBeyondItemModelProvider(packOutput, MekanismBeyond.MODID, event.getExistingFileHelper()));
    }
}
