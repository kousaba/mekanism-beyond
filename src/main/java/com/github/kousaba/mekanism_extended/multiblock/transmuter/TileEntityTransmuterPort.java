package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.registration.ModBlocks;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.lib.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityTransmuterPort extends TileEntityTransmuterCasing {
    public TileEntityTransmuterPort(BlockPos pos, BlockState state) {
        super(ModBlocks.TRANSMUTER_PORT, pos, state);
        // 連打したときに不安定になるのを解消
        delaySupplier = NO_DELAY;
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            boolean oldMode = getActive();
            setActive(!oldMode);
            boolean newMode = getActive();
            Component modeText = Component.translatable(newMode ? "chat.mekanism_extended.output" : "chat.mekanism_extended.input")
                    .withStyle(net.minecraft.ChatFormatting.GRAY);
            player.displayClientMessage(Component.translatable("chat.mekanism_extended.port_mode", modeText), true);
        }
        return InteractionResult.SUCCESS;
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> getMultiblock().getFluidTanks(side);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        // マルチブロック本体が持っているバッテリーを外部に繋ぐ
        return side -> getMultiblock().getEnergyContainers(side);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> getMultiblock().getChemicalTanks(side);
    }

    public void addGasTargetCapability(List<MultiblockData.CapabilityOutputTarget<IChemicalHandler>> targets, Direction side) {
        if (getLevel() instanceof ServerLevel serverLevel) {
            BlockPos neighborPos = getBlockPos().relative(side);
            Direction opposite = side.getOpposite();

            targets.add(new MultiblockData.CapabilityOutputTarget<>(
                    BlockCapabilityCache.create(Capabilities.CHEMICAL.block(), serverLevel, neighborPos, opposite),
                    this::getActive // true (Outputモード) の時だけパイプへ搬出する
            ));
        }
    }

    public void addFluidTargetCapability(List<MultiblockData.CapabilityOutputTarget<IFluidHandler>> targets, Direction side) {
        if (getLevel() instanceof ServerLevel serverLevel) {
            BlockPos neighborPos = getBlockPos().relative(side);
            Direction opposite = side.getOpposite();

            targets.add(new MultiblockData.CapabilityOutputTarget<>(
                    BlockCapabilityCache.create(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, serverLevel, neighborPos, opposite),
                    this::getActive // true (Outputモード) の時だけパイプへ搬出する
            ));
        }
    }
}
