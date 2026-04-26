package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.PBFusionReactorMultiblockData;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiBeyondFusionStats extends GuiBeyondFusionInfo {
    public GuiBeyondFusionStats(EmptyTileContainer<TileEntityBeyondFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected BeyondFusionTab getCurrentTab() {
        return BeyondFusionTab.STATS;
    }

    @Override
    protected void drawForegroundText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.drawForegroundText(guiGraphics, mouseX, mouseY);

        var multiblock = tile.getMultiblock();
        if (multiblock.isFormed()) {
            // 見出し: p-B11 Aneutronic Fusion
            guiGraphics.drawString(this.font, Component.literal("p-B11 Aneutronic Fusion").withStyle(ChatFormatting.LIGHT_PURPLE), 10, 25, 0xFFFFFF, false);

            // 統計情報
            guiGraphics.drawString(this.font, Component.literal("Ignition Temp: 1,000 MK"), 10, 45, 0x404040, false);
            guiGraphics.drawString(this.font, Component.literal("Max Injection Rate: " + PBFusionReactorMultiblockData.MAX_INJECTION), 10, 60, 0x404040, false);

            // 効率: 100% (直接発電のため)
            guiGraphics.drawString(this.font, Component.literal("Direct Energy Conversion: 100%"), 10, 75, 0x404040, false);

            // 燃料情報
            guiGraphics.drawString(this.font, Component.literal("Fuels: Protons & Boron-11"), 10, 90, 0x404040, false);

            // 発電ポテンシャル
            double maxGen = (double) PBFusionReactorMultiblockData.MAX_INJECTION * 0.5;
            guiGraphics.drawString(this.font, Component.literal("Max Output: " + maxGen + " BE/t").withStyle(ChatFormatting.AQUA), 10, 110, 0xFFFFFF, false);
        }
    }
}
