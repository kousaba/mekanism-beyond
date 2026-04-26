package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.content.blocktype.BlockTypeTile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockBeyondFusionPort extends BlockBasicMultiblock<TileEntityBeyondFusionPort> {
    public static final EnumProperty<BeyondFusionPortMode> MODE = EnumProperty.create("mode", BeyondFusionPortMode.class);

    public BlockBeyondFusionPort(BlockTypeTile<TileEntityBeyondFusionPort> type, Properties properties) {
        super(type, properties);
        registerDefaultState(defaultBlockState().setValue(MODE, BeyondFusionPortMode.INPUT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
    }
}
