package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockTransmuterCasing<TILE extends TileEntityTransmuterCasing>
        extends BlockTile<TILE, BlockTypeTile<TILE>> implements IHasDescription {

    public BlockTransmuterCasing(BlockTypeTile<TILE> type) {
        super(type, BlockBehaviour.Properties.of().strength(5.0F, 6.0F).requiresCorrectToolForDrops());
        System.out.println("Block 属性: " + this.type.getAll());
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide && placer instanceof Player player) {
            player.sendSystemMessage(Component.literal("Pos " + pos.toShortString() + " に設置されました！"));
        }
    }


    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> "description.mekanism_beyond.transmuter_casing";
    }
}