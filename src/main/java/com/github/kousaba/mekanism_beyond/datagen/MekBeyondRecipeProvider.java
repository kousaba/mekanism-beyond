package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import mekanism.api.datagen.recipe.builder.NucleosynthesizingRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registries.MekanismItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

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
    }
}
