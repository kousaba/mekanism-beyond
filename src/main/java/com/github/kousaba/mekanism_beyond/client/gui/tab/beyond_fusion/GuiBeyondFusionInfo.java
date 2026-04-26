package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GuiBeyondFusionInfo extends GuiMekanismTile<TileEntityBeyondFusionCasing, EmptyTileContainer<TileEntityBeyondFusionCasing>> {

    protected GuiBeyondFusionInfo(EmptyTileContainer<TileEntityBeyondFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth += 10;
        this.titleLabelY = 5;
    }

    protected abstract BeyondFusionTab getCurrentTab();

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        if (getCurrentTab() != BeyondFusionTab.MAIN) {
            addRenderableWidget(new GuiBeyondFusionTab(this, tile, BeyondFusionTab.MAIN));
        }
        if (getCurrentTab() != BeyondFusionTab.FUEL) {
            addRenderableWidget(new GuiBeyondFusionTab(this, tile, BeyondFusionTab.FUEL));
        }
        if (getCurrentTab() != BeyondFusionTab.STATS) {
            addRenderableWidget(new GuiBeyondFusionTab(this, tile, BeyondFusionTab.STATS));
        }
    }
}
