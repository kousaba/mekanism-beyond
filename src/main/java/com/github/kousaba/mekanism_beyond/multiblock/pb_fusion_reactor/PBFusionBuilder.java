package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import mekanism.common.command.builders.StructureBuilder;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PBFusionBuilder extends StructureBuilder {
    private static final byte[][] GRID = new byte[][]{
            {0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
            {0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0},
            {0, 1, 2, 2, 2, 2, 2, 2, 2, 1, 0},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {0, 1, 2, 2, 2, 2, 2, 2, 2, 1, 0},
            {0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0}
    };

    public PBFusionBuilder() {
        super(11, 11, 11);
    }

    @Override
    protected BlockState getCasing() {
        return MekBeyondBlocks.BEYOND_FUSION_CASING.get().defaultBlockState();
    }

    @Override
    public void build(Level world, BlockPos start, boolean empty) {
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {
                for (int z = 0; z < 11; z++) {
                    BlockPos targetPos = start.offset(x, y, z);

                    // --- 1. 内部の8つの角 (1,1,1)~(9,9,9) の判定 ---
                    boolean isCorner = (x == 1 || x == 9) && (y == 1 || y == 9) && (z == 1 || z == 9);

                    if (isCorner) {
                        world.setBlockAndUpdate(targetPos, getCasing());
                        continue; // 次のブロックへ
                    }

                    // --- 2. 外壁の判定 (既存のGRID) ---
                    if (isWall(x, y, z)) {
                        int h = getH(x, y, z);
                        int v = getV(x, y, z);
                        byte type = GRID[h][v];
                        if (type == 1) world.setBlockAndUpdate(targetPos, getCasing());
                        else if (type == 2)
                            world.setBlockAndUpdate(targetPos, GeneratorsBlocks.REACTOR_GLASS.defaultState());
                    }
                    // --- 3. 内部の判定 ---
                    else if (!empty) {
                        // 磁場安定化コイルの柱 (3x3)
                        if (x >= 4 && x <= 6 && z >= 4 && z <= 6 && y >= 1 && y <= 9) {
                            world.setBlockAndUpdate(targetPos, MekBeyondBlocks.MAGNETIC_STABILIZATION_COIL.get().defaultBlockState());
                        }
                        // Supercharged Coil (4箇所)
                        else if (y == 5 && ((x == 5 && (z == 1 || z == 9)) || (z == 5 && (x == 1 || x == 9)))) {
                            world.setBlockAndUpdate(targetPos, MekanismBlocks.SUPERCHARGED_COIL.defaultState());
                        } else world.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                    } else {
                        world.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }

    private byte getBlockType(int x, int y, int z) {
        // 各面に対してGRIDの値を引く。0以外の値が一つでもあればそれを採用
        if (y == 0 || y == 10) return GRID[x][z];
        if (x == 0 || x == 10) return GRID[z][y];
        if (z == 0 || z == 10) return GRID[x][y];
        return 0;
    }

    private boolean isWall(int x, int y, int z) {
        return x == 0 || x == 10 || y == 0 || y == 10 || z == 0 || z == 10;
    }

    private int getH(int x, int y, int z) {
        if (x == 0 || x == 10) return z; // 左右の壁ならZ軸を横方向とする
        return x; // それ以外（前後、上下）はX軸を横方向とする
    }

    private int getV(int x, int y, int z) {
        if (y == 0 || y == 10) return z; // 上下の壁ならZ軸を縦方向とする
        return y; // それ以外（側面）はY軸を縦方向とする
    }
}
