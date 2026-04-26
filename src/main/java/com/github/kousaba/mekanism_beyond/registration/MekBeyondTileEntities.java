package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionPort;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionPort;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityMagneticStabilizationCoil;
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
    // ADVANCED FUSION REACTOR
    public static final TileEntityTypeRegistryObject<TileEntityAdvancedFusionCasing> ADVANCED_FUSION_CASING =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.ADVANCED_FUSION_CASING, TileEntityAdvancedFusionCasing::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .withSimple(Capabilities.CONFIGURABLE)
                    .build();

    public static final TileEntityTypeRegistryObject<TileEntityAdvancedFusionPort> ADVANCED_FUSION_PORT =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.ADVANCED_FUSION_PORT, TileEntityAdvancedFusionPort::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .withSimple(Capabilities.CONFIGURABLE)
                    .build();
    // PB FUSION REACTOR
    public static final TileEntityTypeRegistryObject<TileEntityBeyondFusionCasing> BEYOND_FUSION_CASING =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.BEYOND_FUSION_CASING, TileEntityBeyondFusionCasing::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .withSimple(Capabilities.CONFIGURABLE)
                    .build();

    public static final TileEntityTypeRegistryObject<TileEntityBeyondFusionPort> BEYOND_FUSION_PORT =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.BEYOND_FUSION_PORT, TileEntityBeyondFusionPort::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .withSimple(Capabilities.CONFIGURABLE)
                    .build();
    public static final TileEntityTypeRegistryObject<TileEntityMagneticStabilizationCoil> MAGNETIC_STABILIZATION_COIL =
            TILE_ENTITY_TYPES.mekBuilder(MekBeyondBlocks.MAGNETIC_STABILIZATION_COIL, TileEntityMagneticStabilizationCoil::new)
                    .clientTicker(TileEntityMekanism::tickClient)
                    .serverTicker(TileEntityMekanism::tickServer)
                    .build();
}
