package com.github.kousaba.mekanism_beyond.client.gui.tab;


import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GuiAdvancedFusionFuel extends GuiAdvancedFusionInfo {
    public GuiAdvancedFusionFuel(EmptyTileContainer<TileEntityAdvancedFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected AdvancedFusionTab getCurrentTab() {
        return AdvancedFusionTab.FUEL;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        var multiblock = tile.getMultiblock();

        addRenderableWidget(new GuiChemicalGauge(() -> multiblock.deuteriumTank, () -> List.of(multiblock.deuteriumTank), GaugeType.STANDARD, this, 25, 64));
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock.neutronTank, () -> List.of(multiblock.neutronTank), GaugeType.STANDARD, this, 133, 64));
    }
}