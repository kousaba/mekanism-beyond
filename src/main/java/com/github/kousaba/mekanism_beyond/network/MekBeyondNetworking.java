package com.github.kousaba.mekanism_beyond.network;


import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MekanismBeyond.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MekBeyondNetworking {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // パケットの登録機を用意
        PayloadRegistrar registrar = event.registrar(MekanismBeyond.MODID).versioned("1.0.0");

        // 1. タブ切り替え用パケット (クライアントからサーバーへ送信)
        registrar.playToServer(
                PacketAdvancedFusionGuiTab.TYPE,
                PacketAdvancedFusionGuiTab.STREAM_CODEC,
                (packet, context) -> packet.handle(context)
        );

        // 2. 注入レート変更用パケット (クライアントからサーバーへ送信)
        registrar.playToServer(
                PacketAdvancedFusionRate.TYPE,
                PacketAdvancedFusionRate.STREAM_CODEC,
                (packet, context) -> packet.handle(context)
        );

        System.out.println("[Mekanism Beyond] Network payloads registered successfully!");
    }
}
