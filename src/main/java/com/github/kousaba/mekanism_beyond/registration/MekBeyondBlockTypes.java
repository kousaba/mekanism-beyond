package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionPort;
import mekanism.common.content.blocktype.BlockTypeTile;

public class MekBeyondBlockTypes {
    public static final BlockTypeTile<TileEntityAdvancedFusionCasing> ADVANCED_FUSION_CASING =
            BlockTypeTile.BlockTileBuilder
                    .createBlock(() -> MekBeyondTileEntities.ADVANCED_FUSION_CASING, () ->
                            "description.mekanism_beyond.advanced_fusion_casing")
                    .withGui(() -> MekBeyondContainerTypes.ADVANCED_FUSION_MAIN, () -> "description.mekanism_beyond.advanced_fusion_casing")
                    .externalMultiblock()
                    .build();
    public static final BlockTypeTile<TileEntityAdvancedFusionPort> ADVANCED_FUSION_PORT = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> MekBeyondTileEntities.ADVANCED_FUSION_PORT, () -> "description.mekanism_beyond.advanced_fusion_port")
            .withGui(() -> MekBeyondContainerTypes.ADVANCED_FUSION_MAIN, () -> "description.mekanism_beyond.advanced_fusion_port")
            .externalMultiblock()
            .build();
}
