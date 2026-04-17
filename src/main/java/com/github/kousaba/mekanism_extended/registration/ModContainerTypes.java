package com.github.kousaba.mekanism_extended.registration;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.TileEntityTransmuterCasing;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class ModContainerTypes {
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(MekanismExtended.MODID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityTransmuterCasing>> TRANSMUTER = CONTAINER_TYPES.custom(ModBlocks.TRANSMUTER_CASING, TileEntityTransmuterCasing.class).build();
}
