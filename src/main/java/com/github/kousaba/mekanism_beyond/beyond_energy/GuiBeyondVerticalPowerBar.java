package com.github.kousaba.mekanism_beyond.beyond_energy;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuiBeyondVerticalPowerBar extends GuiBar<GuiBar.IBarInfoHandler> {
    private static final ResourceLocation ENERGY_BAR = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_BAR, "vertical_power.png");
    private static final int texWidth = 4;
    private static final int texHeight = 52;
    private final double heightScale;

    // 標準コンストラクタ (高さ52)
    public GuiBeyondVerticalPowerBar(IGuiWrapper gui, IBeyondEnergyContainer container, int x, int y) {
        this(gui, container, x, y, 52);
    }

    // 高さ指定コンストラクタ
    public GuiBeyondVerticalPowerBar(IGuiWrapper gui, final IBeyondEnergyContainer container, int x, int y, int desiredHeight) {
        super(ENERGY_BAR, gui, new GuiBar.IBarInfoHandler() {
            @Override
            public Component getTooltip() {
                // BE単位でツールチップを表示 (例: 1.2345 BE / 10000 BE)
                return Component.literal(String.format("%.4f BE / %.0f BE",
                        container.getEnergyBE(), container.getMaxEnergyBE()));
            }

            @Override
            public double getLevel() {
                // double 精度でバーの割合を計算 (0.0 ~ 1.0)
                double max = container.getMaxEnergyBE();
                if (max <= 0) return 0;
                return Math.min(1.0, container.getEnergyBE() / max);
            }
        }, x, y, 4, desiredHeight, false);
        this.heightScale = (double) desiredHeight / 52.0;
    }

    @Override
    protected void renderBarOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, double handlerLevel) {
        // 表示するピクセル数を計算 (0~52)
        int displayInt = (int) (handlerLevel * 52.0);
        if (displayInt > 0) {
            int scaled = calculateScaled(this.heightScale, displayInt);
            // バーの描画 (元のテクスチャの位置から必要な分だけ blit)
            guiGraphics.blit(this.getResource(),
                    this.relativeX + 1,
                    this.relativeY + this.height - 1 - scaled,
                    4, scaled,
                    0.0F, 0.0F,
                    4, displayInt,
                    4, 52);
        }
    }
}