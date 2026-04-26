package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPBFusionCasing extends TileEntityMultiblock<PBFusionReactorMultiblockData> {
    public TileEntityPBFusionCasing(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.ADVANCED_FUSION_CASING, pos, state);
    }

    @Override
    public PBFusionReactorMultiblockData createMultiblock() {
        return new PBFusionReactorMultiblockData(this);
    }

    @Override
    public MultiblockManager<PBFusionReactorMultiblockData> getManager() {
        return MekanismBeyond.pbFusionManager;
    }
}
