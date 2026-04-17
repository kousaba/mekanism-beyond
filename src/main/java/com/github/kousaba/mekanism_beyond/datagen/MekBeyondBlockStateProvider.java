package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MekBeyondBlockStateProvider extends BlockStateProvider {
    public MekBeyondBlockStateProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(MekBeyondBlocks.TRANSMUTER_CASING.get());

        // Mekanismの親モデルのパスを定義
        ResourceLocation templatePath = ResourceLocation.fromNamespaceAndPath("mekanism", "block/template/cube_all_led");

        // Input用モデル (active=false)
        // new ModelFile.UncheckedModelFile を使うことで存在チェックを回避する
        ModelFile portInput = models().getBuilder("transmuter_port")
            .parent(new ModelFile.UncheckedModelFile(templatePath))
            .texture("all", modLoc("block/transmuter_port"))
            .texture("led", modLoc("block/transmuter_port_led"));

        // Output用モデル (active=true)
        ModelFile portOutput = models().getBuilder("transmuter_port_output")
            .parent(new ModelFile.UncheckedModelFile(templatePath))
            .texture("all", modLoc("block/transmuter_port_output"))
            .texture("led", modLoc("block/transmuter_port_output_led"));

        // --- 3. BlockStateの紐付け ---
        getVariantBuilder(MekBeyondBlocks.TRANSMUTER_PORT.get())
            .forAllStates(state -> {
                Property<?> property = state.getBlock().getStateDefinition().getProperty("active");
                boolean isActive = false;
                if (property instanceof BooleanProperty boolProp) {
                    isActive = state.getValue(boolProp);
                }

                return ConfiguredModel.builder()
                    .modelFile(isActive ? portOutput : portInput)
                    .build();
            });
    }
}