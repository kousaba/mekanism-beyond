package com.github.kousaba.mekanism_beyond.client.gui.tab;

import com.github.kousaba.mekanism_beyond.MekBeyondLang;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.github.kousaba.mekanism_beyond.network.PacketAdvancedFusionGuiTab;
import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.client.gui.element.tab.TabType;
import mekanism.client.render.lib.ColorAtlas;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public enum AdvancedFusionTab implements TabType<TileEntityAdvancedFusionCasing> {

    // それぞれのタブに、Mekanism標準の色を割り当てます
    MAIN(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "button/home.png"), MekBeyondLang.MAIN_TAB, 6, 0, SpecialColors.TAB_CONFIGURATION),
    HEAT(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "heat.png"), MekBeyondLang.HEAT_TAB, 34, 1, SpecialColors.TAB_MULTIBLOCK_STATS),
    FUEL(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "gui/fuel.png"), MekBeyondLang.FUEL_TAB, 62, 2, SpecialColors.TAB_MULTIBLOCK_STATS),
    STATS(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "stats.png"), MekBeyondLang.STATS_TAB, 90, 3, SpecialColors.TAB_MULTIBLOCK_STATS);

    private final ResourceLocation path;
    private final ILangEntry description;
    private final int yPos;
    private final int tabId;
    private final ColorAtlas.ColorRegistryObject color; // 追加

    AdvancedFusionTab(ResourceLocation path, ILangEntry description, int yPos, int tabId, ColorAtlas.ColorRegistryObject color) {
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
    public void onClick(TileEntityAdvancedFusionCasing tile) {
        PacketUtils.sendToServer(new PacketAdvancedFusionGuiTab(tabId, tile.getBlockPos()));
    }

    @Override
    public Component getDescription() {
        return description.translate();
    }

    @Override
    public int getYPos() {
        return yPos;
    }

    // 修正: null ではなく、enumで指定した色を返す
    @Override
    public ColorAtlas.ColorRegistryObject getTabColor() {
        return color;
    }
}
