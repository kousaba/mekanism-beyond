package com.github.kousaba.mekanism_extended.datagen;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import mekanism.api.datamaps.chemical.attribute.CooledCoolant;
import mekanism.api.datamaps.chemical.attribute.HeatedCoolant;
import mekanism.common.registries.MekanismDataMapTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.internal.NeoForgeDataMapsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, MekanismExtended.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, MekanismExtended.MODID, "en_us"));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, MekanismExtended.MODID, event.getExistingFileHelper()));
    }
}
