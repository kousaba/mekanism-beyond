package com.github.kousaba.mekanism_extended;

import com.github.kousaba.mekanism_extended.client.gui.GuiTransmuter;
import com.github.kousaba.mekanism_extended.registration.ModContainerTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MekanismExtended.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    public static void init(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void renderScreens(RegisterMenuScreensEvent event) {
        event.register(ModContainerTypes.TRANSMUTER.get(), GuiTransmuter::new);
    }
}
