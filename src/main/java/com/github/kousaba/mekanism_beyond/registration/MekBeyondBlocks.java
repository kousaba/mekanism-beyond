package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.BlockTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterPort;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterBlockTypes;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class MekBeyondBlocks {
    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(MekanismBeyond.MODID);

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerBlock(String name, Supplier<? extends BLOCK> blockSupplier) {
        return BLOCKS.register(name, blockSupplier, ItemBlockTooltip::new);
    }

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
}
