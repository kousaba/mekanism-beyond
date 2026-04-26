package com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion;

import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class GuiAdvancedFusionHeat extends GuiAdvancedFusionInfo {
    public GuiAdvancedFusionHeat(EmptyTileContainer<TileEntityAdvancedFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected AdvancedFusionTab getCurrentTab() {
        return AdvancedFusionTab.HEAT;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        var multiblock = tile.getMultiblock();

        // プラズマ温度ゲージ
        addRenderableWidget(new GuiNumberGauge(new GuiNumberGauge.INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(Fluids.LAVA, MekanismRenderer.FluidTextureType.STILL);
            }

            @Override
            public double getLevel() {
                return multiblock.getPlasmaTemp();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1.0, getLevel() / 500_000_000.0);
            }

            @Override
            public Component getText() {
                return Component.literal("Plasma: ").append(MekanismUtils.getTemperatureDisplay(getLevel(), UnitDisplayUtils.TemperatureUnit.KELVIN, true));
            }
        }, GaugeType.STANDARD, this, 12, 50));

        // ケーシング温度ゲージ
        addRenderableWidget(new GuiNumberGauge(new GuiNumberGauge.INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(Fluids.LAVA, MekanismRenderer.FluidTextureType.STILL);
            }

            @Override
            public double getLevel() {
                return multiblock.getCaseTemp();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1.0, getLevel() / 500_000_000.0);
            }

            @Override
            public Component getText() {
                return Component.literal("Casing: ").append(MekanismUtils.getTemperatureDisplay(getLevel(), UnitDisplayUtils.TemperatureUnit.KELVIN, true));
            }
        }, GaugeType.STANDARD, this, 66, 50));

        // 水・蒸気タンク
        addRenderableWidget(new GuiFluidGauge(() -> multiblock.waterTank, () -> List.of(multiblock.waterTank), GaugeType.SMALL, this, 120, 84));
        addRenderableWidget(new GuiChemicalGauge(() -> multiblock.steamTank, () -> List.of(multiblock.steamTank), GaugeType.SMALL, this, 156, 84));
    }
}
