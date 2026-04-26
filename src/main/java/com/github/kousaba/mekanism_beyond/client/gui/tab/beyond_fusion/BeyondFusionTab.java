package com.github.kousaba.mekanism_beyond.client.gui.tab.beyond_fusion;

import com.github.kousaba.mekanism_beyond.MekBeyondLang;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.client.gui.element.tab.TabType;
import mekanism.client.render.lib.ColorAtlas;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum BeyondFusionTab implements TabType<TileEntityBeyondFusionCasing> {
    MAIN(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "button/home.png"), MekBeyondLang.MAIN_TAB, 6, 0, SpecialColors.TAB_CONFIGURATION),
    FUEL(ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "gui/fuel.png"), MekBeyondLang.FUEL_TAB, 34, 1, SpecialColors.TAB_MULTIBLOCK_STATS),
    STATS(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "stats.png"), MekBeyondLang.STATS_TAB, 62, 2, SpecialColors.TAB_MULTIBLOCK_STATS);

    private final ResourceLocation path;
    private final ILangEntry description;
    private final int yPos;
    private final int tabId;
    private final ColorAtlas.ColorRegistryObject color;

    BeyondFusionTab(ResourceLocation path, ILangEntry description, int yPos, int tabId, ColorAtlas.ColorRegistryObject color) {
        this.path = path;
        this.description = description;
        this.yPos = yPos;
        this.tabId = tabId;
        this.color = color;
    }

    @Override
    public ResourceLocation getResource() {
        return path;
    }

    @Override
    public void onClick(TileEntityBeyondFusionCasing tile) {
        PacketUtils.sendToServer(new PacketBeyondFusionGuiTab(tabId, tile.getBlockPos()));
    }

    @Override
    public Component getDescription() {
        return description.translate();
    }

    @Override
    public int getYPos() {
        return yPos;
    }

    @Override
    public ColorAtlas.ColorRegistryObject getTabColor() {
        return color;
    }
}
