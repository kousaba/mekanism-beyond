package com.github.kousaba.mekanism_beyond.multiblock.transmuter;


import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.common.command.builders.StructureBuilder;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TransmuterBuilder extends StructureBuilder {

    public TransmuterBuilder() {
        // 最大サイズ: 幅17, 高さ18, 奥行17
        super(17, 18, 17);
    }

    @Override
    public void build(Level world, BlockPos start, boolean empty) {
        // 1. 外枠（角と辺）の構築
        this.buildFrame(world, start);
        // 2. 壁面の構築
        this.buildWalls(world, start);

        // 内部空間の定義 (Y座標)
        // Y=0: 床, Y=17: 天井
        // Y=1: 下部コイル層
        // Y=2〜15: 空気層
        // Y=16: 上部コイル層

        // 3. 中央の空気層 (Y=2 〜 15)
        this.buildInteriorLayers(world, start, 2, 15, Blocks.AIR.defaultBlockState());

        if (!empty) {
            // 4. 下部コイル層 (Y=1) と 上部コイル層 (Y=16) の構築
            buildCoilLayer(world, start, 1);
            buildCoilLayer(world, start, 16);
        } else {
            // emptyフラグがtrueの場合は内部をすべて空気にする
            this.buildInteriorLayer(world, start, 1, Blocks.AIR.defaultBlockState());
            this.buildInteriorLayer(world, start, 16, Blocks.AIR.defaultBlockState());
        }
    }

    /**
     * 指定した高さ(y)にコイル層を構築する。中心にSupercharged、周囲にElectromagneticを配置。
     */
    private void buildCoilLayer(Level world, BlockPos start, int y) {
        // 内部は 1 〜 (size-2) の範囲。サイズ17なら 1 〜 15。
        // 中心は (0+16)/2 = 8。
        int centerX = 8;
        int centerZ = 8;

        for (int x = 1; x <= 15; x++) {
            for (int z = 1; z <= 15; z++) {
                BlockPos targetPos = start.offset(x, y, z);
                if (x == centerX && z == centerZ) {
                    // 中心軸は Supercharged Coil
                    world.setBlockAndUpdate(targetPos, MekanismBlocks.SUPERCHARGED_COIL.defaultState());
                } else {
                    // それ以外は Electromagnetic Coil
                    world.setBlockAndUpdate(targetPos, GeneratorsBlocks.ELECTROMAGNETIC_COIL.defaultState());
                }
            }
        }
    }

    @Override
    protected BlockState getCasing() {
        // 使うブロックの指定
        return MekBeyondBlocks.TRANSMUTER_CASING.get().defaultBlockState();
    }
}
