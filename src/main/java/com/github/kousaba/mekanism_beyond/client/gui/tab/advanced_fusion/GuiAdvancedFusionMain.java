package com.github.kousaba.mekanism_beyond.client.gui.tab.advanced_fusion;

import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.github.kousaba.mekanism_beyond.network.PacketAdvancedFusionRate;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.text.InputValidator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiAdvancedFusionMain extends GuiAdvancedFusionInfo {
    private GuiTextField injectionRateField;

    public GuiAdvancedFusionMain(EmptyTileContainer<TileEntityAdvancedFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected AdvancedFusionTab getCurrentTab() {
        return AdvancedFusionTab.MAIN;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();

        // 注入レートの入力欄 (数字のみ許可)
        injectionRateField = addRenderableWidget(new GuiTextField(this, 64, 40, 48, 12));
        injectionRateField.setMaxLength(3);
        injectionRateField.setInputValidator(InputValidator.DIGIT);
        injectionRateField.setEnterHandler(this::setInjectionRate);
    }

    private void setInjectionRate() {
        if (!injectionRateField.getText().isEmpty()) {
            int rate = Integer.parseInt(injectionRateField.getText());
            PacketUtils.sendToServer(new PacketAdvancedFusionRate(tile.getBlockPos(), rate));
        }
        injectionRateField.setText("");
    }

    @Override
    protected void drawForegroundText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
        var multiblock = tile.getMultiblock();

        // 燃焼ステータス
        Component status = multiblock.isBurning() ? Component.literal("Burning").withStyle(net.minecraft.ChatFormatting.DARK_GREEN)
                : Component.literal("Offline").withStyle(net.minecraft.ChatFormatting.DARK_RED);
        guiGraphics.drawString(this.font, Component.literal("Status: ").append(status), 40, 20, 0x404040, false);

        guiGraphics.drawString(this.font, Component.literal("Injection Rate: " + multiblock.getInjectionRate()), 40, 60, 0x404040, false);
    }
}
