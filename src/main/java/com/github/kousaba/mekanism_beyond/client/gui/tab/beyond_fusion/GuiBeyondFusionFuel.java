package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GuiBeyondFusionFuel extends GuiBeyondFusionInfo {
    public GuiBeyondFusionFuel(EmptyTileContainer<TileEntityBeyondFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected BeyondFusionTab getCurrentTab() {
        return BeyondFusionTab.FUEL;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        var multiblock = tile.getMultiblock();

        // 陽子タンク
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock.protonTank, () -> List.of(multiblock.protonTank), GaugeType.STANDARD, this, 25, 64));
        // ホウ素タンク
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock.boronTank, () -> List.of(multiblock.boronTank), GaugeType.STANDARD, this, 133, 64));
    }
}
