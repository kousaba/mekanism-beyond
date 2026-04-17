package com.github.kousaba.mekanism_beyond;

import com.github.kousaba.mekanism_beyond.client.gui.GuiTransmuter;
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
    }

    @SubscribeEvent
    public static void renderBER(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(MekBeyondTileEntities.TRANSMUTER_CASING.get(), RenderTransmuter::new);
        event.registerBlockEntityRenderer(MekBeyondTileEntities.TRANSMUTER_PORT.get(), RenderTransmuter::new);
    }
}
