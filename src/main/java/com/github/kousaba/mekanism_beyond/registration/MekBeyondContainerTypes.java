package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class MekBeyondContainerTypes {
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(MekanismBeyond.MODID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityTransmuterCasing>> TRANSMUTER = CONTAINER_TYPES.custom(MekBeyondBlocks.TRANSMUTER_CASING, TileEntityTransmuterCasing.class).build();

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityAdvancedFusionCasing>> ADVANCED_FUSION_MAIN =
            CONTAINER_TYPES.registerEmpty("advanced_fusion_main", TileEntityAdvancedFusionCasing.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityAdvancedFusionCasing>> ADVANCED_FUSION_HEAT =
            CONTAINER_TYPES.registerEmpty("advanced_fusion_heat", TileEntityAdvancedFusionCasing.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityAdvancedFusionCasing>> ADVANCED_FUSION_FUEL =
            CONTAINER_TYPES.registerEmpty("advanced_fusion_fuel", TileEntityAdvancedFusionCasing.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityAdvancedFusionCasing>> ADVANCED_FUSION_STATS =
            CONTAINER_TYPES.registerEmpty("advanced_fusion_stats", TileEntityAdvancedFusionCasing.class);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityBeyondFusionCasing>> BEYOND_FUSION_MAIN =
            CONTAINER_TYPES.registerEmpty("beyond_fusion_main", TileEntityBeyondFusionCasing.class);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityBeyondFusionCasing>> BEYOND_FUSION_FUEL =
            CONTAINER_TYPES.registerEmpty("beyond_fusion_fuel", TileEntityBeyondFusionCasing.class);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityBeyondFusionCasing>> BEYOND_FUSION_STATS =
            CONTAINER_TYPES.registerEmpty("beyond_fusion_stats", TileEntityBeyondFusionCasing.class);
}