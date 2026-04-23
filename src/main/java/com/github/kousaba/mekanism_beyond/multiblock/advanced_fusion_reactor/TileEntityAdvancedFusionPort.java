package com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
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

public class TileEntityAdvancedFusionPort extends TileEntityAdvancedFusionCasing {
    public TileEntityAdvancedFusionPort(BlockPos pos, BlockState state) {
        super(MekBeyondBlocks.ADVANCED_FUSION_PORT, pos, state);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> {
            if (getLevel() == null || !getMultiblock().isFormed()) return java.util.Collections.emptyList();
            // 現在のモードを取得
            AdvancedFusionPortMode mode = getBlockState().getValue(BlockAdvancedFusionPort.MODE);

            if (mode == AdvancedFusionPortMode.COOLANT) return java.util.List.of(getMultiblock().waterTank);
            return java.util.Collections.emptyList();
        };
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> {
            if (getLevel() == null || !getMultiblock().isFormed()) return java.util.Collections.emptyList();
            AdvancedFusionPortMode mode = getBlockState().getValue(BlockAdvancedFusionPort.MODE);

            if (mode == AdvancedFusionPortMode.INPUT) return java.util.List.of(getMultiblock().deuteriumTank);
            if (mode == AdvancedFusionPortMode.NEUTRON) return java.util.List.of(getMultiblock().neutronTank);
            if (mode == AdvancedFusionPortMode.OUTPUT) return java.util.List.of(getMultiblock().steamTank);
            return java.util.Collections.emptyList();
        };
    }

    // --- 自動搬出制御 ---

    public void addGasTargetCapability(List<MultiblockData.CapabilityOutputTarget<IChemicalHandler>> targets, Direction side) {
        if (getLevel() instanceof ServerLevel serverLevel) {
            targets.add(new MultiblockData.CapabilityOutputTarget<>(
                    BlockCapabilityCache.create(Capabilities.CHEMICAL.block(), serverLevel, getBlockPos().relative(side), side.getOpposite()),
                    // NEUTRON モードの時だけ中性子を排出する
                    () -> getBlockState().getValue(BlockAdvancedFusionPort.MODE) == AdvancedFusionPortMode.NEUTRON
            ));
        }
    }

    public void addFluidTargetCapability(List<MultiblockData.CapabilityOutputTarget<IFluidHandler>> targets, Direction side) {
        if (getLevel() instanceof ServerLevel serverLevel) {
            targets.add(new MultiblockData.CapabilityOutputTarget<>(
                    BlockCapabilityCache.create(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, serverLevel, getBlockPos().relative(side), side.getOpposite()),
                    // OUTPUT モードの時だけ蒸気を排出する
                    () -> getBlockState().getValue(BlockAdvancedFusionPort.MODE) == AdvancedFusionPortMode.OUTPUT
            ));
        }
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        return side -> {
            if (getLevel() == null || !getMultiblock().isFormed()) return java.util.Collections.emptyList();

            // INPUT モードの時だけ熱を受け取れるようにする
            AdvancedFusionPortMode mode = getBlockState().getValue(BlockAdvancedFusionPort.MODE);
            if (mode == AdvancedFusionPortMode.INPUT) {
                return getMultiblock().getHeatCapacitors(side);
            }
            return java.util.Collections.emptyList();
        };
    }

    // --- Configurator での切り替え ---

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            AdvancedFusionPortMode currentMode = getBlockState().getValue(BlockAdvancedFusionPort.MODE);
            AdvancedFusionPortMode nextMode = currentMode.getNext();

            getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockAdvancedFusionPort.MODE, nextMode));

            // チャットメッセージ
            this.invalidateCapabilities();
            getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
            player.displayClientMessage(Component.translatable("mekanism_beyond.port_mode." + nextMode.getSerializedName()).withStyle(net.minecraft.ChatFormatting.GRAY), true);
        }
        return InteractionResult.SUCCESS;
    }
}
