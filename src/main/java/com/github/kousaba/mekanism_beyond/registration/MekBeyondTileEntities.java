package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterPort;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

public class MekBeyondTileEntities {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(MekanismBeyond.MODID);
    public static final TileEntityTypeRegistryObject<TileEntityTransmuterCasing> TRANSMUTER_CASING =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.TRANSMUTER_CASING, TileEntityTransmuterCasing::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .withSimple(Capabilities.CONFIGURABLE) // ボイラーと同様に追加
                    .build();

    public static final TileEntityTypeRegistryObject<TileEntityTransmuterPort> TRANSMUTER_PORT =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.TRANSMUTER_PORT, TileEntityTransmuterPort::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .withSimple(Capabilities.CONFIGURABLE)
                    .build();
}
