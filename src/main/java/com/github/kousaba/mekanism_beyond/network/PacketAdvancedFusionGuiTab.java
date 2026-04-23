package com.github.kousaba.mekanism_beyond.network;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketAdvancedFusionGuiTab(int tabId, BlockPos pos) implements IMekanismPacket {
    public static final CustomPacketPayload.Type<PacketAdvancedFusionGuiTab> TYPE = new CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MekanismBeyond.MODID, "advanced_fusion_tab"));
    public static final StreamCodec<ByteBuf, PacketAdvancedFusionGuiTab> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketAdvancedFusionGuiTab::tabId,
            BlockPos.STREAM_CODEC, PacketAdvancedFusionGuiTab::pos,
            PacketAdvancedFusionGuiTab::new
    );

    @Override
    public CustomPacketPayload.Type<PacketAdvancedFusionGuiTab> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        TileEntityAdvancedFusionCasing tile = WorldUtils.getTileEntity(TileEntityAdvancedFusionCasing.class, player.level(), pos);

        if (tile != null) {
            // タブIDに基づいて開くコンテナを決定
            var containerType = switch (tabId) {
                case 1 -> MekBeyondContainerTypes.ADVANCED_FUSION_HEAT;
                case 2 -> MekBeyondContainerTypes.ADVANCED_FUSION_FUEL;
                case 3 -> MekBeyondContainerTypes.ADVANCED_FUSION_STATS;
                default -> MekBeyondContainerTypes.ADVANCED_FUSION_MAIN;
            };

            // サーバー側で新しいGUIを開く
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return tile.getDisplayName(); // ケーシングの名前をGUIタイトルにする
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    return new EmptyTileContainer<>(containerType, id, inv, tile);
                }
            }, pos);
        }
    }
}
