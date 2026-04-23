package com.github.kousaba.mekanism_beyond.beyond_energy;


import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class GuiBeyondEnergyTab extends GuiTexturedElement {
    private final IBeyondEnergyContainer container;
    private final DoubleSupplier lastUsageFE;

    public GuiBeyondEnergyTab(IGuiWrapper gui, IBeyondEnergyContainer container, DoubleSupplier lastUsageFE) {
        super(MekanismUtils.getResource(ResourceType.GUI_TAB, "energy_info.png"), gui, -26, 137, 26, 26);
        this.container = container;
        this.lastUsageFE = lastUsageFE;
    }

    @Override
    protected ResourceLocation getResource() {
        return BeyondEnergyUnit.getConfigured().getIcon();
    }

    @Override
    public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blit(this.getResource(), this.relativeX, this.relativeY, 0, 0, this.width, this.height, this.width, this.height);
    }

    @Override
    public void updateTooltip(int mouseX, int mouseY) {
        List<Component> info = new ArrayList<>();
        double usageBE = container.getLastUsageBE();
        System.out.println("UsageBE: " + usageBE);
        info.add(MekanismLang.USING.translate(String.format("%.4f BE", usageBE)));
        info.add(MekanismLang.MAX.translate(String.format("%.2f BE", container.getMaxEnergyBE())));

        // 1.21方式のコンポーネント結合
        MutableComponent combined = Component.empty();
        for (int i = 0; i < info.size(); i++) {
            combined.append(info.get(i));
            if (i < info.size() - 1) {
                combined.append(Component.literal("\n")); // 改行を挟む
            }
        }
        this.setTooltip(Tooltip.create(combined));
    }

    private String formatByUnit(double fe, BeyondEnergyUnit unit) {
        return switch (unit) {
            case JOULES -> MekanismUtils.getEnergyDisplayShort((long) (fe * BeyondEnergyUnit.J_PER_FE)).getString();
            case FE -> TextUtils.format(fe) + " FE"; // DoubleValueFormatter の代わり
            case BE -> BeyondEnergyDisplay.format(fe / BeyondEnergyUnit.FE_PER_BE);
        };
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            BeyondEnergyUnit.setConfigured(BeyondEnergyUnit.getConfigured().getNext());
        } else if (button == 1) {
            BeyondEnergyUnit.setConfigured(BeyondEnergyUnit.getConfigured().getPrevious());
        }
    }

    @Override
    public boolean isValidClickButton(int button) {
        return button == 0 || button == 1;
    }
}
