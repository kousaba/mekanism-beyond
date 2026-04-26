package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.BlockAdvancedFusionPort;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.BlockBeyondFusionPort;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityMagneticStabilizationCoil;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.BlockTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterPort;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterBlockTypes;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class MekBeyondBlocks {
    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(MekanismBeyond.MODID);
    public static final BlockRegistryObject<BlockTransmuterCasing<TileEntityTransmuterCasing>, ItemBlockTooltip<BlockTransmuterCasing<TileEntityTransmuterCasing>>> TRANSMUTER_CASING =
            BLOCKS.register("transmuter_casing",
                    () -> new BlockTransmuterCasing<>(TransmuterBlockTypes.TRANSMUTER_CASING),
                    ItemBlockTooltip::new);
    // Portの登録
    // ここで型引数を <TileEntityTransmuterPort> にすることでエラーが解消されます
    public static final BlockRegistryObject<BlockTransmuterCasing<TileEntityTransmuterPort>, ItemBlockTooltip<BlockTransmuterCasing<TileEntityTransmuterPort>>> TRANSMUTER_PORT =
            BLOCKS.register("transmuter_port",
                    () -> new BlockTransmuterCasing<>(TransmuterBlockTypes.TRANSMUTER_PORT),
                    ItemBlockTooltip::new);
    // Advanced Fusion Reactor
    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityAdvancedFusionCasing>, ItemBlockTooltip<BlockBasicMultiblock<TileEntityAdvancedFusionCasing>>> ADVANCED_FUSION_CASING =
            BLOCKS.register("advanced_fusion_casing", () -> new BlockBasicMultiblock<>(MekBeyondBlockTypes.ADVANCED_FUSION_CASING, properties -> properties.mapColor(MapColor.COLOR_GRAY)), ItemBlockTooltip::new);
    public static final BlockRegistryObject<BlockAdvancedFusionPort, ItemBlockTooltip<BlockAdvancedFusionPort>> ADVANCED_FUSION_PORT =
            BLOCKS.register("advanced_fusion_port", () -> new BlockAdvancedFusionPort(MekBeyondBlockTypes.ADVANCED_FUSION_PORT, net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY)), ItemBlockTooltip::new);
    // PBFusionReactor
    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityMagneticStabilizationCoil>, ItemBlockTooltip<BlockBasicMultiblock<TileEntityMagneticStabilizationCoil>>> MAGNETIC_STABILIZATION_COIL =
            BLOCKS.register("magnetic_stabilization_coil",
                    () -> new BlockBasicMultiblock<>(MekBeyondBlockTypes.MAGNETIC_STABILIZATION_COIL,
                            properties -> properties.mapColor(MapColor.COLOR_BLUE)),
                    ItemBlockTooltip::new);
    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityBeyondFusionCasing>, ItemBlockTooltip<BlockBasicMultiblock<TileEntityBeyondFusionCasing>>> BEYOND_FUSION_CASING =
            BLOCKS.register("beyond_fusion_casing",
                    () -> new BlockBasicMultiblock<>(MekBeyondBlockTypes.BEYOND_FUSION_CASING,
                            properties -> properties.mapColor(MapColor.COLOR_BLACK)),
                    ItemBlockTooltip::new);
    public static final BlockRegistryObject<BlockBeyondFusionPort, ItemBlockTooltip<BlockBeyondFusionPort>> BEYOND_FUSION_PORT =
            BLOCKS.register("beyond_fusion_port",
                    () -> new BlockBeyondFusionPort(MekBeyondBlockTypes.BEYOND_FUSION_PORT,
                            net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)),
                    ItemBlockTooltip::new);

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerBlock(String name, Supplier<? extends BLOCK> blockSupplier) {
        return BLOCKS.register(name, blockSupplier, ItemBlockTooltip::new);
    }
}
