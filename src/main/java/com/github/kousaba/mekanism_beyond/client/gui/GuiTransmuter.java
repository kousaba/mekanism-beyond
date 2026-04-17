package com.github.kousaba.mekanism_beyond.client.gui;

import com.github.kousaba.mekanism_beyond.beyond_energy.GuiBeyondVerticalPowerBar;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterMultiblockData;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GuiTransmuter extends GuiMekanism<MekanismTileContainer<TileEntityTransmuterCasing>> {
    public GuiTransmuter(MekanismTileContainer<TileEntityTransmuterCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
        imageWidth = 200;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();

        TileEntityTransmuterCasing tile = menu.getTileEntity();
        TransmuterMultiblockData multiblock = tile.getMultiblock();

        // 1. 左側: 水のゲージ (Input)
        addRenderableWidget(new GuiFluidGauge(
                () -> multiblock.waterTank, () -> List.of(multiblock.waterTank),
                GaugeType.STANDARD, this, 10, 13
        ));

        // 2. 中央: 液晶画面 (幅を100に広げ、中心付近に配置)
        addRenderableWidget(new GuiInnerScreen(this, 35, 15, 100, 60, () -> {
            double speedMultiplier = 1.0 + (multiblock.getCoilCount() * TransmuterMultiblockData.COIL_SPEED_BONUS);
            return List.of(
                    Component.literal("Active: " + multiblock.isActive()),
                    Component.literal("Coils: " + multiblock.getCoilCount()),
                    Component.literal("Prob: " + String.format("%.2f%%", multiblock.getProbability() * 100)),
                    Component.literal("Spd: " + String.format("%.1fx", speedMultiplier)),
                    Component.literal("Use: " + multiblock.currentWaterUsage + "mB/t")
            );
        }));

        // 3. 右側 (1つ目): 重水のゲージ (Output)
        addRenderableWidget(new GuiFluidGauge(
                () -> multiblock.heavyWaterTank, () -> List.of(multiblock.heavyWaterTank),
                GaugeType.STANDARD, this, 142, 13
        ));

        // 4. 右側 (2つ目): ウラン水のゲージ (Output)
        addRenderableWidget(new GuiChemicalGauge(
                () -> multiblock.uraniumWaterTank, () -> List.of(multiblock.uraniumWaterTank),
                GaugeType.STANDARD, this, 162, 13
        ));

        // 5. 一番右端: 電力バー (Input)
        addRenderableWidget(new GuiBeyondVerticalPowerBar(this, multiblock.energyContainer, 184, 15));
    }

    @Override
    protected void drawForegroundText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics); // タイトル(ブロック名)を描画
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
