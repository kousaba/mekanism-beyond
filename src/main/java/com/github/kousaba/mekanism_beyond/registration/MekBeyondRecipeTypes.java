package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MekBeyondRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MekanismBeyond.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MekanismBeyond.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TransmuterRecipe>> TRANSMUTING = TYPES.register("transmuting", () -> new RecipeType<TransmuterRecipe>() {
        @Override
        public String toString() {
            return "transmuting";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TransmuterRecipe>> TRANSMUTING_SERIALIZER = SERIALIZERS.register("transmuting", () -> new RecipeSerializer<TransmuterRecipe>() {

        private final MapCodec<TransmuterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStackIngredient.CODEC.fieldOf("inputFluid").forGetter(TransmuterRecipe::inputFluid),
                com.mojang.serialization.Codec.DOUBLE.fieldOf("energyUsageBE").forGetter(TransmuterRecipe::energyUsageBE),
                FluidStack.CODEC.fieldOf("outputFluid").forGetter(TransmuterRecipe::outputFluid),
                ChemicalStack.CODEC.fieldOf("outputChemical").forGetter(TransmuterRecipe::outputChemical),
                com.mojang.serialization.Codec.DOUBLE.fieldOf("minProbability").forGetter(TransmuterRecipe::minProbability),
                com.mojang.serialization.Codec.DOUBLE.fieldOf("maxProbability").forGetter(TransmuterRecipe::maxProbability),
                com.mojang.serialization.Codec.DOUBLE.fieldOf("minSpeedMultiplier").forGetter(TransmuterRecipe::minSpeedMultiplier),
                com.mojang.serialization.Codec.DOUBLE.fieldOf("maxSpeedMultiplier").forGetter(TransmuterRecipe::maxSpeedMultiplier)
        ).apply(instance, TransmuterRecipe::new));

        private final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, TransmuterRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    FluidStackIngredient.STREAM_CODEC.encode(buf, recipe.inputFluid());
                    buf.writeDouble(recipe.energyUsageBE());
                    FluidStack.STREAM_CODEC.encode(buf, recipe.outputFluid());
                    ChemicalStack.STREAM_CODEC.encode(buf, recipe.outputChemical());
                    // --- ここを追加！ 4つの double を書き込む ---
                    buf.writeDouble(recipe.minProbability());
                    buf.writeDouble(recipe.maxProbability());
                    buf.writeDouble(recipe.minSpeedMultiplier());
                    buf.writeDouble(recipe.maxSpeedMultiplier());
                },
                buf -> new TransmuterRecipe(
                        FluidStackIngredient.STREAM_CODEC.decode(buf),
                        buf.readDouble(),
                        FluidStack.STREAM_CODEC.decode(buf),
                        ChemicalStack.STREAM_CODEC.decode(buf),
                        buf.readDouble(),
                        buf.readDouble(),
                        buf.readDouble(),
                        buf.readDouble()
                )
        );

        @Override
        public MapCodec<TransmuterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, TransmuterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    });
}
