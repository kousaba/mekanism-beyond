package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.registration.ModContainerTypes;
import com.github.kousaba.mekanism_extended.registration.ModTileEntities;
import mekanism.common.content.blocktype.BlockTypeTile;

public class TransmuterBlockTypes {
    public static final BlockTypeTile<TileEntityTransmuterCasing> TRANSMUTER_CASING =
            (BlockTypeTile<TileEntityTransmuterCasing>) BlockTypeTile.BlockTileBuilder.createBlock(
                            () -> ModTileEntities.TRANSMUTER_CASING,
                            () -> "description.mekanism_extended.transmuter_casing"
                    )
                    .withGui(() -> ModContainerTypes.TRANSMUTER, () -> "description.mekanism_extended.transmuter_casing")
                    .externalMultiblock().build();
    public static final BlockTypeTile<TileEntityTransmuterPort> TRANSMUTER_PORT =
            (BlockTypeTile<TileEntityTransmuterPort>) BlockTypeTile.BlockTileBuilder.createBlock(
                            () -> ModTileEntities.TRANSMUTER_PORT,
                            () -> "description.mekanism_extended.transmuter_port"
                    )
                    .withGui(() -> ModContainerTypes.TRANSMUTER, () -> "description.mekanism_extended.transmuter_port")
                    .externalMultiblock()
                    .with(mekanism.common.block.attribute.Attributes.ACTIVE)
                    .build();
}
