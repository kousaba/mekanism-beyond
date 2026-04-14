package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.registration.ModBlocks;
import com.github.kousaba.mekanism_extended.registration.ModTileEntities;
import mekanism.api.IConfigurable;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityTransmuterCasing extends TileEntityMultiblock<TransmuterMultiblockData> {
    public TileEntityTransmuterCasing(BlockPos pos, BlockState state){
        super(ModBlocks.TRANSMUTER_CASING, pos, state);
    }
    public TileEntityTransmuterCasing(Holder<Block> blockProvider, BlockPos pos, BlockState state){
        super(blockProvider, pos, state);
    }

    @NotNull
    @Override
    public TransmuterMultiblockData createMultiblock() {
        return new TransmuterMultiblockData(this);
    }

    @Override
    public void onAdded(){
        System.out.println("--- onAdded 開始: " + this.getBlockPos().toShortString() + " ---");
        super.onAdded();
        System.out.println("--- onAdded 終了 ---");
    }

    @Override
    public void onLoad() {
        super.onLoad(); // これが重要
        if (!isRemote()) {
            // サーバー側で動いている場合、チャットにメッセージを出す
            Player player = getLevel().getNearestPlayer(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 10, false);
            if (player != null) {
                player.sendSystemMessage(Component.literal("§a[Server] onLoad実行: Managerを呼び出します"));
            }

            // 強制的にマネージャーにアクセスして形成チェックを依頼
            getManager();
        }
    }

    @Override
    public MultiblockManager<TransmuterMultiblockData> getManager(){
        System.out.println("DEBUG: getManager() has been accessed!");
        return TransmuterManager.INSTANCE;
    }
}
