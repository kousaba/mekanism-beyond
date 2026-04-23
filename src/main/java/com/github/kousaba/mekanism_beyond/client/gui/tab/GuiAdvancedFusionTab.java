package com.github.kousaba.mekanism_beyond.client.gui.tab;


import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiTabElementType;

public class GuiAdvancedFusionTab extends GuiTabElementType<TileEntityAdvancedFusionCasing, AdvancedFusionTab> {
    public GuiAdvancedFusionTab(IGuiWrapper gui, TileEntityAdvancedFusionCasing tile, AdvancedFusionTab type) {
        super(gui, tile, type);
    }
}