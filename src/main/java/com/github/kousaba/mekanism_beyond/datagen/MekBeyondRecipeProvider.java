package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterRecipeBuilder;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondChemicals;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.datagen.recipe.builder.NucleosynthesizingRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.CompletableFuture;

public class MekBeyondRecipeProvider extends RecipeProvider {
    public MekBeyondRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registies) {
        super(output, registies);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        NucleosynthesizingRecipeBuilder.nucleosynthesizing(
                IngredientCreatorAccess.item().from(MekanismItems.ATOMIC_ALLOY),
                IngredientCreatorAccess.chemicalStack().fromHolder(MekanismChemicals.ANTIMATTER, 10),
                MekanismBeyond.NANO_ALLOY.get().getDefaultInstance(),
                500,
                false
        ).build(output, ResourceLocation.parse(MekanismBeyond.MODID + ":nucleosynthesizing/nano_alloy"));
        NucleosynthesizingRecipeBuilder.nucleosynthesizing(
                IngredientCreatorAccess.item().from(MekanismBlocks.STEEL_CASING), // 鉄鋼ケーシングがベース
                IngredientCreatorAccess.chemicalStack().fromHolder(MekanismChemicals.ANTIMATTER, 35),
                MekBeyondBlocks.TRANSMUTER_CASING.get().asItem().getDefaultInstance(),
                1000, // 制作時間
                false
        ).build(output, ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "nucleosynthesizing/transmuter_casing"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MekBeyondBlocks.TRANSMUTER_PORT.get())
                .pattern(" C ")
                .pattern("CUC")
                .pattern(" C ")
                .define('C', MekBeyondBlocks.TRANSMUTER_CASING.get())
                .define('U', MekanismItems.ULTIMATE_CONTROL_CIRCUIT) // 究極の制御回路
                .unlockedBy("has_transmuter_casing", has(MekBeyondBlocks.TRANSMUTER_CASING.get()))
                .save(output);
        TransmuterRecipeBuilder.transmuting(
                        IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                        0.05,
                        new FluidStack(MekanismFluids.HEAVY_WATER, 10),
                        new ChemicalStack(MekBeyondChemicals.URANIUM_WATER.get(), 50)
                )
                .probability(0.20, 0.50) // 最小サイズで0.1%、最大サイズで5%
                .speed(1.0, 10.0)         // コイル0個で1倍、コイル448個で10倍
                .save(output, ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "water_conversion"));
        TransmuterRecipeBuilder.transmuting(
                        IngredientCreatorAccess.fluid().from(MekanismFluids.SUPERHEATED_SODIUM.get(), 1000),
                        200,
                        new FluidStack(MekanismFluids.SUPERHEATED_SODIUM, 999),
                        new ChemicalStack(MekanismChemicals.ANTIMATTER.get(), 100)
                )
                .probability(0.0000001, 0.01)
                .speed(1.0, 2.0)
                .save(output, ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "antimatter_extraction"));
    }
}
