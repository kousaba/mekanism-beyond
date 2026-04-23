package com.github.kousaba.mekanism_beyond.datagen;

import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.AdvancedFusionPortMode;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.BlockAdvancedFusionPort;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MekBeyondBlockStateProvider extends BlockStateProvider {
    public MekBeyondBlockStateProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(MekBeyondBlocks.TRANSMUTER_CASING.get());
        simpleBlock(MekBeyondBlocks.ADVANCED_FUSION_CASING.get());

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

        // 4つのモデルを作成
        ModelFile portInputModel = models().cubeAll("advanced_fusion_port", modLoc("block/advanced_fusion_port"));
        ModelFile portCoolantModel = models().cubeAll("advanced_fusion_port_coolant", modLoc("block/advanced_fusion_port_coolant"));
        ModelFile portNeutronModel = models().cubeAll("advanced_fusion_port_neutron", modLoc("block/advanced_fusion_port_neutron"));
        ModelFile portOutputModel = models().cubeAll("advanced_fusion_port_output", modLoc("block/advanced_fusion_port_output"));

        // プロパティに応じてモデルを切り替える
        getVariantBuilder(MekBeyondBlocks.ADVANCED_FUSION_PORT.get())
                .forAllStates(state -> {
                    AdvancedFusionPortMode mode = state.getValue(BlockAdvancedFusionPort.MODE);
                    ModelFile file = switch (mode) {
                        case INPUT -> portInputModel;
                        case COOLANT -> portCoolantModel;
                        case NEUTRON -> portNeutronModel;
                        case OUTPUT -> portOutputModel;
                    };
                    return ConfiguredModel.builder().modelFile(file).build();
                });
    }
}