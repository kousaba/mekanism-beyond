package com.github.kousaba.mekanism_extended.registration;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.TileEntityTransmuterPort;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModTileEntities {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(MekanismExtended.MODID);
    public static final TileEntityTypeRegistryObject<TileEntityTransmuterCasing> TRANSMUTER_CASING =
        TILE_ENTITY_TYPES.mekBuilder(ModBlocks.TRANSMUTER_CASING, TileEntityTransmuterCasing::new)
            .withSimple(Capabilities.CONFIGURABLE) // ボイラーと同様に追加
            .build();

public static final TileEntityTypeRegistryObject<TileEntityTransmuterPort> TRANSMUTER_PORT =
        TILE_ENTITY_TYPES.mekBuilder(ModBlocks.TRANSMUTER_PORT, TileEntityTransmuterPort::new)
            .withSimple(Capabilities.CONFIGURABLE)
            .build();
}
