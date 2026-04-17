package com.github.kousaba.mekanism_beyond;

import com.github.kousaba.mekanism_beyond.datagen.DataGenerators;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterMultiblockData;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterValidator;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondChemicals;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondContainerTypes;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondTileEntities;
import com.mojang.logging.LogUtils;
import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MekanismBeyond.MODID)
public class MekanismBeyond {
    public static final String MODID = "mekanism_beyond";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> NANO_ALLOY = ITEMS.registerSimpleItem("nano_alloy", new Item.Properties());
    public static final MultiblockManager<TransmuterMultiblockData> transmuterManager = new MultiblockManager("transmuter", MultiblockCache::new, TransmuterValidator::new);
    // --- ブロック登録 (シンボルを解決) ---


    public MekanismBeyond(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        MekBeyondBlocks.BLOCKS.register(modEventBus);
        MekBeyondTileEntities.TILE_ENTITY_TYPES.register(modEventBus);
        MekBeyondContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MekBeyondChemicals.CHEMICALS.register(modEventBus);
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(ClientEvent::init);
        System.out.println("Force Initializing Manager: " + transmuterManager);
        LOGGER.info("Mekansim Beyond loaded!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}