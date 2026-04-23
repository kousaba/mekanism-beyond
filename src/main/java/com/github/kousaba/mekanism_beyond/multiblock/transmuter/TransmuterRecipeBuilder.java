package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

public class TransmuterRecipeBuilder {
    private final FluidStackIngredient inputFluid;
    private final double energyUsage;
    private final FluidStack outputFluid;
    private final ChemicalStack outputChemical;
    private double minProb, maxProb, minSpeed, maxSpeed;

    private TransmuterRecipeBuilder(FluidStackIngredient input, double energy, FluidStack outFluid, ChemicalStack outChem) {
        this.inputFluid = input;
        this.energyUsage = energy;
        this.outputFluid = outFluid;
        this.outputChemical = outChem;
    }

    public static TransmuterRecipeBuilder transmuting(FluidStackIngredient input, double energy, FluidStack outFluid, ChemicalStack outChem) {
        return new TransmuterRecipeBuilder(input, energy, outFluid, outChem);
    }

    public TransmuterRecipeBuilder probability(double min, double max) {
        this.minProb = min;
        this.maxProb = max;
        return this;
    }

    public TransmuterRecipeBuilder speed(double min, double max) {
        this.minSpeed = min;
        this.maxSpeed = max;
        return this;
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        output.accept(id, new TransmuterRecipe(inputFluid, energyUsage, outputFluid, outputChemical, minProb, maxProb, minSpeed, maxSpeed), null);
    }
}