package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;


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

public class TileEntityBeyondFusionCasing extends TileEntityMultiblock<PBFusionReactorMultiblockData> {
    public TileEntityBeyondFusionCasing(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.BEYOND_FUSION_CASING, pos, state);
    }

    public TileEntityBeyondFusionCasing(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    public PBFusionReactorMultiblockData createMultiblock() {
        return new PBFusionReactorMultiblockData(this);
    }

    @Override
    public MultiblockManager<PBFusionReactorMultiblockData> getManager() {

        return MekanismBeyond.pbFusionManager;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        PBFusionReactorMultiblockData data = getMultiblock();
        if (data != null) {
            // レートと燃焼状態の同期
            container.track(SyncableInt.create(data::getInjectionRate, data::setInjectionRate));
            container.track(SyncableBoolean.create(data::isBurning, data::setBurning));

            // GUIで発電量を表示するための同期
            container.track(SyncableDouble.create(() -> data.lastGenerationRateBE, val -> data.lastGenerationRateBE = val));

            // 燃料タンクの同期
            container.track(SyncableChemicalStack.create(data.protonTank));
            container.track(SyncableChemicalStack.create(data.boronTank));

            // BEバッファの同期
            container.track(SyncableDouble.create(data.energyContainer::getLastUsageBE, data.energyContainer::setLastUsageBE));
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
