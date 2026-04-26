package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.beyond_energy.GuiBeyondEnergyTab;
import com.github.kousaba.mekanism_beyond.beyond_energy.GuiBeyondVerticalPowerBar;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import com.github.kousaba.mekanism_beyond.network.PacketBeyondFusionRate;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.text.InputValidator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiBeyondFusionMain extends GuiBeyondFusionInfo {
    private GuiTextField injectionRateField;

    public GuiBeyondFusionMain(EmptyTileContainer<TileEntityBeyondFusionCasing> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected BeyondFusionTab getCurrentTab() {
        return BeyondFusionTab.MAIN;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        var multiblock = tile.getMultiblock();

        // 注入レート入力
        injectionRateField = addRenderableWidget(new GuiTextField(this, 64, 40, 48, 12));
        injectionRateField.setMaxLength(3);
        injectionRateField.setInputValidator(InputValidator.DIGIT);
        injectionRateField.setEnterHandler(this::setInjectionRate);

        // BE電力タブ (直接発電の情報を表示)
        addRenderableWidget(new GuiBeyondEnergyTab(this,
                multiblock.energyContainer,
                () -> multiblock.lastGenerationRateBE
        ));

        // 電力バー
        addRenderableWidget(new GuiBeyondVerticalPowerBar(this, multiblock.energyContainer, 172, 15));
    }

    private void setInjectionRate() {
        if (!injectionRateField.getText().isEmpty()) {
            int rate = Integer.parseInt(injectionRateField.getText());
            // PacketAdvancedFusionRate を流用するか、専用を作って飛ばす
            PacketUtils.sendToServer(new PacketBeyondFusionRate(tile.getBlockPos(), rate));
        }
        injectionRateField.setText("");
    }

    @Override
    protected void drawForegroundText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
        var multiblock = tile.getMultiblock();
        Component status = multiblock.isBurning() ? Component.literal("Generating").withStyle(ChatFormatting.AQUA)
                : Component.literal("Offline").withStyle(ChatFormatting.DARK_RED);
        guiGraphics.drawString(this.font, Component.literal("Status: ").append(status), 40, 20, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Injection Rate: " + multiblock.getInjectionRate()), 40, 60, 0x404040, false);
    }
}
