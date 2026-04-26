package com.github.kousaba.mekanism_beyond;

import com.github.kousaba.mekanism_beyond.client.gui.GuiTransmuter;
import com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion.GuiAdvancedFusionFuel;
import com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion.GuiAdvancedFusionHeat;
import com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion.GuiAdvancedFusionMain;
import com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion.GuiAdvancedFusionStats;
import com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion.GuiBeyondFusionFuel;
import com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion.GuiBeyondFusionMain;
import com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion.GuiBeyondFusionStats;
import com.github.kousaba.mekanism_beyond.client.render.RenderAdvancedFusionReactor;
import com.github.kousaba.mekanism_beyond.client.render.RenderPBFusionReactor;
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
        event.register(MekBeyondContainerTypes.BEYOND_FUSION_MAIN.get(), GuiBeyondFusionMain::new);
        event.register(MekBeyondContainerTypes.BEYOND_FUSION_FUEL.get(), GuiBeyondFusionFuel::new);
        event.register(MekBeyondContainerTypes.BEYOND_FUSION_STATS.get(), GuiBeyondFusionStats::new);
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
        event.registerBlockEntityRenderer(MekBeyondTileEntities.BEYOND_FUSION_CASING.get(), RenderPBFusionReactor::new);
        event.registerBlockEntityRenderer(MekBeyondTileEntities.BEYOND_FUSION_PORT.get(), RenderPBFusionReactor::new);
        event.registerBlockEntityRenderer(MekBeyondTileEntities.MAGNETIC_STABILIZATION_COIL.get(), RenderPBFusionReactor::new);
    }
}
