package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiTabElementType;

public class GuiBeyondFusionTab extends GuiTabElementType<TileEntityBeyondFusionCasing, BeyondFusionTab> {
    public GuiBeyondFusionTab(IGuiWrapper gui, TileEntityBeyondFusionCasing tile, BeyondFusionTab type) {
        super(gui, tile, type);
    }
}
