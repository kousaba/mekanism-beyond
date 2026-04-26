package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondContainerTypes;
import io.netty.buffer.ByteBuf;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketBeyondFusionGuiTab(int tabId, BlockPos pos) implements IMekanismPacket {
    public static final CustomPacketPayload.Type<PacketBeyondFusionGuiTab> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "beyond_fusion_tab"));
    public static final StreamCodec<ByteBuf, PacketBeyondFusionGuiTab> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketBeyondFusionGuiTab::tabId,
            BlockPos.STREAM_CODEC, PacketBeyondFusionGuiTab::pos,
            PacketBeyondFusionGuiTab::new
    );

    @Override
    public CustomPacketPayload.Type<PacketBeyondFusionGuiTab> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        TileEntityBeyondFusionCasing tile = WorldUtils.getTileEntity(TileEntityBeyondFusionCasing.class, player.level(), pos);

        if (tile != null) {
            var containerType = switch (tabId) {
                case 1 -> MekBeyondContainerTypes.BEYOND_FUSION_FUEL;
                case 2 -> MekBeyondContainerTypes.BEYOND_FUSION_STATS;
                default -> MekBeyondContainerTypes.BEYOND_FUSION_MAIN;
            };

            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return tile.getDisplayName();
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    return new EmptyTileContainer<>(containerType, id, inv, tile);
                }
            }, pos);
        }
    }
}
