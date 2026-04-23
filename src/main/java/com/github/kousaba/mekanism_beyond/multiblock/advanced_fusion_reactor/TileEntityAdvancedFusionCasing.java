package com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.chemical.SyncableChemicalStack;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityAdvancedFusionCasing extends TileEntityMultiblock<AdvancedFusionReactorMultiblockData> {
    public TileEntityAdvancedFusionCasing(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.ADVANCED_FUSION_CASING, pos, state);
    }

    public TileEntityAdvancedFusionCasing(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    public AdvancedFusionReactorMultiblockData createMultiblock() {
        return new AdvancedFusionReactorMultiblockData(this);
    }

    @Override
    public MultiblockManager<AdvancedFusionReactorMultiblockData> getManager() {
        return MekanismBeyond.advancedFusionManager;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        AdvancedFusionReactorMultiblockData data = getMultiblock();
        if (data != null) {
            // 温度・レート・燃焼状態の同期
            container.track(SyncableDouble.create(data::getPlasmaTemp, val -> {
            })); // クライアントは受信のみ
            container.track(SyncableDouble.create(data::getCaseTemp, val -> {
            }));
            container.track(SyncableInt.create(data::getInjectionRate, data::setInjectionRate));
            container.track(SyncableBoolean.create(data::isBurning, data::setBurning));

            // 4つのタンクの同期
            container.track(SyncableChemicalStack.create(data.deuteriumTank));
            container.track(SyncableChemicalStack.create(data.neutronTank));
            container.track(mekanism.common.inventory.container.sync.SyncableFluidStack.create(data.waterTank));
            container.track(SyncableChemicalStack.create(data.steamTank));
        }
    }

    @Override
    public InteractionResult onRightClick(Player player) {
        if (!getMultiblock().isFormed()) {
            return InteractionResult.PASS;
        }
        return super.onRightClick(player);
    }
}
