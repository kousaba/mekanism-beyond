package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class MekBeyondContainerTypes {
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(MekanismBeyond.MODID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityTransmuterCasing>> TRANSMUTER = CONTAINER_TYPES.custom(MekBeyondBlocks.TRANSMUTER_CASING, TileEntityTransmuterCasing.class).build();
}
