package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.registration.ModBlocks;
import mekanism.api.chemical.ChemicalUtils;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.util.ChemicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import java.util.List;

public class TileEntityTransmuterPort extends TileEntityTransmuterCasing{
    public TileEntityTransmuterPort(BlockPos pos, BlockState state){
        super(ModBlocks.TRANSMUTER_PORT, pos, state);
    }

    public void addGasTargetCapability(List<MultiblockData.CapabilityOutputTarget<IChemicalHandler>> targets, Direction side){
        if (getLevel() instanceof ServerLevel serverLevel) {
            // 隣接座標
            BlockPos neighborPos = getBlockPos().relative(side);
            // 隣接面（相手から見たこちらの面）
            Direction opposite = side.getOpposite();

            // BlockCapabilityCache を作成してターゲットリストに追加
            targets.add(new MultiblockData.CapabilityOutputTarget<>(
                BlockCapabilityCache.create(
                    Capabilities.CHEMICAL.block(), // IChemicalHandlerのCapability
                    serverLevel,
                    neighborPos,
                    opposite
                ),
                () -> !isRemoved() // 自分が存在している間は有効
            ));
        }
    }
}
