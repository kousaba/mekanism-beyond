package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondContainerTypes;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondTileEntities;
import mekanism.common.content.blocktype.BlockTypeTile;

public class TransmuterBlockTypes {
    public static final BlockTypeTile<TileEntityTransmuterCasing> TRANSMUTER_CASING =
            (BlockTypeTile<TileEntityTransmuterCasing>) BlockTypeTile.BlockTileBuilder.createBlock(
                            () -> MekBeyondTileEntities.TRANSMUTER_CASING,
                            () -> "description.mekanism_beyond.transmuter_casing"
                    )
                    .withGui(() -> MekBeyondContainerTypes.TRANSMUTER, () -> "description.mekanism_beyond.transmuter_casing")
                    .externalMultiblock().build();
    public static final BlockTypeTile<TileEntityTransmuterPort> TRANSMUTER_PORT =
            (BlockTypeTile<TileEntityTransmuterPort>) BlockTypeTile.BlockTileBuilder.createBlock(
                            () -> MekBeyondTileEntities.TRANSMUTER_PORT,
                            () -> "description.mekanism_beyond.transmuter_port"
                    )
                    .withGui(() -> MekBeyondContainerTypes.TRANSMUTER, () -> "description.mekanism_beyond.transmuter_port")
                    .externalMultiblock()
                    .with(mekanism.common.block.attribute.Attributes.ACTIVE)
                    .build();
}
