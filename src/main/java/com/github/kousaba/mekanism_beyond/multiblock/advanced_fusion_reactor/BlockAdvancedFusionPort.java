package com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor;

import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.content.blocktype.BlockTypeTile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockAdvancedFusionPort extends BlockBasicMultiblock<TileEntityAdvancedFusionPort> {

    // 4つのモードを保存するプロパティ
    public static final EnumProperty<AdvancedFusionPortMode> MODE = EnumProperty.create("mode", AdvancedFusionPortMode.class);

    public BlockAdvancedFusionPort(BlockTypeTile<TileEntityAdvancedFusionPort> type, Properties properties) {
        super(type, properties);
        // デフォルトは Input(重水素) モード
        registerDefaultState(defaultBlockState().setValue(MODE, AdvancedFusionPortMode.INPUT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
    }


}
