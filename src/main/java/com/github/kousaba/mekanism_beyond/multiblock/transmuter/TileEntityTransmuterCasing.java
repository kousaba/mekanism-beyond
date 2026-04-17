package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityTransmuterCasing extends TileEntityMultiblock<TransmuterMultiblockData> {
    public TileEntityTransmuterCasing(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.TRANSMUTER_CASING, pos, state);
    }

    public TileEntityTransmuterCasing(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @NotNull
    @Override
    public TransmuterMultiblockData createMultiblock() {
        return new TransmuterMultiblockData(this);
    }

    @Override
    public void onAdded() {
        super.onAdded();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();

        // 引数ではなく、getMultiblock() で取得する
        TransmuterMultiblockData multiblock = getMultiblock();

        // 構成済みの場合のみアニメーション計算を行う
        if (multiblock.isFormed()) {
            float targetScale = (float) multiblock.waterTank.getFluidAmount() / multiblock.BASE_WATER_CAPACITY;

            // 水位を滑らかに更新 (prevScaleはDataクラスのメンバ変数)
            if (Math.abs(multiblock.prevScale - targetScale) > 0.01) {
                multiblock.prevScale = (multiblock.prevScale * 9 + targetScale) / 10;
            } else {
                multiblock.prevScale = targetScale;
            }
        }
    }

    @Override
    public MultiblockManager<TransmuterMultiblockData> getManager() {
        return MekanismBeyond.transmuterManager;
    }
}
