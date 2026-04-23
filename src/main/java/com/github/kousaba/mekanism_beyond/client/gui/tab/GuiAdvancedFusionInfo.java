package com.github.kousaba.mekanism_beyond.client.gui.tab;


import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class GuiAdvancedFusionInfo extends GuiMekanismTile<TileEntityAdvancedFusionCasing, EmptyTileContainer<TileEntityAdvancedFusionCasing>> {

    protected GuiAdvancedFusionInfo(EmptyTileContainer<TileEntityAdvancedFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth += 10; // 枠を少し広くする (Mekanismスタイル)
        this.titleLabelY = 5;
    }

    // 各子クラスで「今のタブ」を指定させる
    protected abstract AdvancedFusionTab getCurrentTab();

    @Override
    protected void addGuiElements() {
        super.addGuiElements();

        // 自分が現在開いているタブ "以外" のタブボタンを画面左側に配置する
        if (getCurrentTab() != AdvancedFusionTab.MAIN) {
            addRenderableWidget(new GuiAdvancedFusionTab(this, tile, AdvancedFusionTab.MAIN));
        }
        if (getCurrentTab() != AdvancedFusionTab.HEAT) {
            addRenderableWidget(new GuiAdvancedFusionTab(this, tile, AdvancedFusionTab.HEAT));
        }
        if (getCurrentTab() != AdvancedFusionTab.FUEL) {
            addRenderableWidget(new GuiAdvancedFusionTab(this, tile, AdvancedFusionTab.FUEL));
        }
        if (getCurrentTab() != AdvancedFusionTab.STATS) {
            addRenderableWidget(new GuiAdvancedFusionTab(this, tile, AdvancedFusionTab.STATS));
        }
    }
}