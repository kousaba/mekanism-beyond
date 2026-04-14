package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.datagen.ModLanguageProvider;
import com.github.kousaba.mekanism_extended.registration.ModTileEntities;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registries.MekanismContainerTypes;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class TransmuterBlockTypes {
    public static final BlockTypeTile<TileEntityTransmuterCasing> TRANSMUTER_CASING =
            (BlockTypeTile<TileEntityTransmuterCasing>) BlockTypeTile.BlockTileBuilder.createBlock(
                    () -> ModTileEntities.TRANSMUTER_CASING,
                    () -> "description.mekanism_extended.transmuter_casing"
            )
                    .withGui(() -> null, () -> null).externalMultiblock().build();
    public static final BlockTypeTile<TileEntityTransmuterPort> TRANSMUTER_PORT =
        (BlockTypeTile<TileEntityTransmuterPort>) BlockTypeTile.BlockTileBuilder.createBlock(
            () -> ModTileEntities.TRANSMUTER_PORT,
            () -> "description.mekanism_extended.transmuter_port"
        ).withGui(() -> null, () -> null)
        .externalMultiblock()
        .build();
}
