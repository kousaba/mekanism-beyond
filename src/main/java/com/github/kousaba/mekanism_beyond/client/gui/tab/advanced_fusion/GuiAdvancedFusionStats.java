package com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion;


import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiAdvancedFusionStats extends GuiAdvancedFusionInfo {
    public GuiAdvancedFusionStats(EmptyTileContainer<TileEntityAdvancedFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected AdvancedFusionTab getCurrentTab() {
        return AdvancedFusionTab.STATS;
    }

    @Override
    protected void drawForegroundText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.drawForegroundText(guiGraphics, mouseX, mouseY);

        var multiblock = tile.getMultiblock();
        if (multiblock.isFormed()) {
            guiGraphics.drawString(this.font, Component.literal("Advanced D-D Fusion"), 10, 25, 0x0000CD, false);
            guiGraphics.drawString(this.font, Component.literal("Ignition Temp: 100 MK"), 10, 40, 0x404040, false);
            guiGraphics.drawString(this.font, Component.literal("Max Injection Rate: 200"), 10, 55, 0x404040, false);
            guiGraphics.drawString(this.font, Component.literal("Steam multiplier: 3.0x"), 10, 70, 0x404040, false);
            guiGraphics.drawString(this.font, Component.literal("Neutrons / Deuterium: 10"), 10, 85, 0x404040, false);
        }
    }
}