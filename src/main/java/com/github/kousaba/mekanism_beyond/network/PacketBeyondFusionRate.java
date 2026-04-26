package com.github.kousaba.mekanism_beyond.network;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import io.netty.buffer.ByteBuf;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketBeyondFusionRate(BlockPos pos, int rate) implements IMekanismPacket {
    public static final CustomPacketPayload.Type<PacketBeyondFusionRate> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "beyond_fusion_rate"));
    public static final StreamCodec<ByteBuf, PacketBeyondFusionRate> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketBeyondFusionRate::pos,
            ByteBufCodecs.INT, PacketBeyondFusionRate::rate,
            PacketBeyondFusionRate::new
    );

    @Override
    public CustomPacketPayload.Type<PacketBeyondFusionRate> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        // ここを Beyond 用の TileEntity に指定する
        TileEntityBeyondFusionCasing tile = WorldUtils.getTileEntity(TileEntityBeyondFusionCasing.class, player.level(), pos);

        if (tile != null && tile.getMultiblock().isFormed()) {
            tile.getMultiblock().setInjectionRate(rate);
        }
    }
}
