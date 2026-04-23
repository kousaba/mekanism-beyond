package com.github.kousaba.mekanism_beyond.network;


import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import io.netty.buffer.ByteBuf;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketAdvancedFusionRate(BlockPos pos, int rate) implements IMekanismPacket {

    public static final CustomPacketPayload.Type<PacketAdvancedFusionRate> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "advanced_fusion_rate"));
    public static final StreamCodec<ByteBuf, PacketAdvancedFusionRate> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketAdvancedFusionRate::pos,
            ByteBufCodecs.INT, PacketAdvancedFusionRate::rate,
            PacketAdvancedFusionRate::new
    );

    @Override
    public CustomPacketPayload.Type<PacketAdvancedFusionRate> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        TileEntityAdvancedFusionCasing tile = WorldUtils.getTileEntity(TileEntityAdvancedFusionCasing.class, player.level(), pos);

        if (tile != null && tile.getMultiblock().isFormed()) {
            // マルチブロックのレートを更新する
            tile.getMultiblock().setInjectionRate(rate);
        }
    }
}