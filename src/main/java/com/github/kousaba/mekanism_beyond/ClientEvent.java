package com.github.kousaba.mekanism_beyond;

import com.github.kousaba.mekanism_beyond.client.gui.GuiTransmuter;
import com.github.kousaba.mekanism_beyond.client.gui.tab.GuiAdvancedFusionFuel;
import com.github.kousaba.mekanism_beyond.client.gui.tab.GuiAdvancedFusionHeat;
import com.github.kousaba.mekanism_beyond.client.gui.tab.GuiAdvancedFusionMain;
import com.github.kousaba.mekanism_beyond.client.gui.tab.GuiAdvancedFusionStats;
import com.github.kousaba.mekanism_beyond.client.render.RenderAdvancedFusionReactor;
import com.github.kousaba.mekanism_beyond.client.render.RenderTransmuter;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondContainerTypes;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondTileEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MekanismBeyond.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    public static void init(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void renderScreens(RegisterMenuScreensEvent event) {
        event.register(MekBeyondContainerTypes.TRANSMUTER.get(), GuiTransmuter::new);
        event.register(MekBeyondContainerTypes.ADVANCED_FUSION_MAIN.get(), GuiAdvancedFusionMain::new);
        event.register(MekBeyondContainerTypes.ADVANCED_FUSION_HEAT.get(), GuiAdvancedFusionHeat::new);
        event.register(MekBeyondContainerTypes.ADVANCED_FUSION_FUEL.get(), GuiAdvancedFusionFuel::new);
        event.register(MekBeyondContainerTypes.ADVANCED_FUSION_STATS.get(), GuiAdvancedFusionStats::new);
    }

    @SubscribeEvent
    public static void renderBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MekBeyondTileEntities.TRANSMUTER_CASING.get(), RenderTransmuter::new);
        event.registerBlockEntityRenderer(MekBeyondTileEntities.TRANSMUTER_PORT.get(), RenderTransmuter::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MekBeyondTileEntities.ADVANCED_FUSION_CASING.get(), RenderAdvancedFusionReactor::new);
        event.registerBlockEntityRenderer(MekBeyondTileEntities.ADVANCED_FUSION_PORT.get(), RenderAdvancedFusionReactor::new);
    }
}
