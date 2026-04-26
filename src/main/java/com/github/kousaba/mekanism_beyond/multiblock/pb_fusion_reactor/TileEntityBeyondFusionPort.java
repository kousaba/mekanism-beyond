package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.api.IContentsListener;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityBeyondFusionPort extends TileEntityBeyondFusionCasing {
    public TileEntityBeyondFusionPort(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.BEYOND_FUSION_PORT, pos, state);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> {
            if (getLevel() == null || !getMultiblock().isFormed()) return java.util.Collections.emptyList();
            BeyondFusionPortMode mode = getBlockState().getValue(BlockBeyondFusionPort.MODE);

            // INPUT モードの時だけ、燃料（陽子とホウ素）の搬入を許可
            if (mode == BeyondFusionPortMode.INPUT) {
                return java.util.List.of(getMultiblock().protonTank, getMultiblock().boronTank);
            }
            return java.util.Collections.emptyList();
        };
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return side -> {
            if (getLevel() == null || !getMultiblock().isFormed()) return java.util.Collections.emptyList();
            BeyondFusionPortMode mode = getBlockState().getValue(BlockBeyondFusionPort.MODE);

            // OUTPUT モードの時だけ、BE（電力）の搬出を許可
            if (mode == BeyondFusionPortMode.OUTPUT) {
                return java.util.List.of(getMultiblock().energyContainer);
            }
            return java.util.Collections.emptyList();
        };
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            BeyondFusionPortMode currentMode = getBlockState().getValue(BlockBeyondFusionPort.MODE);
            BeyondFusionPortMode nextMode = currentMode.getNext();

            // モードを切り替えてブロック状態を更新
            getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockBeyondFusionPort.MODE, nextMode));

            this.invalidateCapabilities();
            getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());

            // コンフィギュレーターでクリックした時のチャットメッセージ（水色で表示）
            player.displayClientMessage(Component.translatable("mekanism_beyond.pb_port_mode." + nextMode.getSerializedName()).withStyle(net.minecraft.ChatFormatting.AQUA), true);
        }
        return InteractionResult.SUCCESS;
    }
}
