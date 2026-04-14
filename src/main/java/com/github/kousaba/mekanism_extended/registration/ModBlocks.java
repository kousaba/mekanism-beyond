package com.github.kousaba.mekanism_extended.registration;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.BlockTransmuterCasing;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.TileEntityTransmuterPort;
import com.github.kousaba.mekanism_extended.multiblock.transmuter.TransmuterBlockTypes;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(MekanismExtended.MODID);
    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerBlock(String name, Supplier<? extends BLOCK> blockSupplier) {
        return BLOCKS.register(name, blockSupplier, ItemBlockTooltip::new);
    }public static final BlockRegistryObject<BlockTransmuterCasing<TileEntityTransmuterCasing>, ItemBlockTooltip<BlockTransmuterCasing<TileEntityTransmuterCasing>>> TRANSMUTER_CASING =
            BLOCKS.register("transmuter_casing",
                () -> new BlockTransmuterCasing<>(TransmuterBlockTypes.TRANSMUTER_CASING),
                ItemBlockTooltip::new);

    // Portの登録
    // ここで型引数を <TileEntityTransmuterPort> にすることでエラーが解消されます
    public static final BlockRegistryObject<BlockTransmuterCasing<TileEntityTransmuterPort>, ItemBlockTooltip<BlockTransmuterCasing<TileEntityTransmuterPort>>> TRANSMUTER_PORT =
            BLOCKS.register("transmuter_port",
                () -> new BlockTransmuterCasing<>(TransmuterBlockTypes.TRANSMUTER_PORT),
                ItemBlockTooltip::new);
}
