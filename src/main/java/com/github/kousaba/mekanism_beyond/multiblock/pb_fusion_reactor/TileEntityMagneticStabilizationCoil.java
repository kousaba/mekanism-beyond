package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityMagneticStabilizationCoil extends TileEntityBeyondFusionCasing {
    public TileEntityMagneticStabilizationCoil(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.MAGNETIC_STABILIZATION_COIL, pos, state);
    }
}
