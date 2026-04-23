package com.github.kousaba.mekanism_beyond.multiblock.transmuter;


import com.github.kousaba.mekanism_beyond.registration.MekBeyondRecipeTypes;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@NothingNullByDefault
public record TransmuterRecipe(
        FluidStackIngredient inputFluid,
        double energyUsageBE,
        FluidStack outputFluid,
        ChemicalStack outputChemical,
        double minProbability,
        double maxProbability,
        double minSpeedMultiplier,
        double maxSpeedMultiplier
) implements Recipe<TransmuterRecipeInput> {

    @Override
    public boolean matches(TransmuterRecipeInput input, Level level) {
        return true;
    }

    @Override
    public ItemStack assemble(TransmuterRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MekBeyondRecipeTypes.TRANSMUTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MekBeyondRecipeTypes.TRANSMUTING.get();
    }
}