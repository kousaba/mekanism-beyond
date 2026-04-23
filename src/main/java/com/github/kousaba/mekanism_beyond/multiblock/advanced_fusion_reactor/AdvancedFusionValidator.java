package com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.Structure;
import mekanism.common.lib.multiblock.StructureHelper;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.EnumSet;

public class AdvancedFusionValidator extends CuboidStructureValidator<AdvancedFusionReactorMultiblockData> {
    private static final VoxelCuboid BOUNDS = new VoxelCuboid(7, 7, 7);
    private static final byte[][] ALLOWED_GRID = new byte[][]{
            {0, 0, 1, 1, 1, 0, 0},
            {0, 1, 2, 2, 2, 1, 0},
            {1, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 1},
            {0, 1, 2, 2, 2, 1, 0},
            {0, 0, 1, 1, 1, 0, 0}
    };

    @Override
    protected FormationProtocol.StructureRequirement getStructureRequirement(BlockPos pos) {
        VoxelCuboid.WallRelative relative = this.cuboid.getWallRelative(pos);
        if (relative.isWall()) {
            // 現在の面に応じた2次元座標を取得
            Structure.Axis axis = Structure.Axis.get(this.cuboid.getSide(pos));
            Structure.Axis h = axis.horizontal();
            Structure.Axis v = axis.vertical();
            BlockPos relativePos = pos.subtract(this.cuboid.getMinPos());

            int gridValue = ALLOWED_GRID[h.getCoord(relativePos)][v.getCoord(relativePos)];
            // SPSのロジックに従い、GRIDの値から要求タイプを返す
            return FormationProtocol.StructureRequirement.REQUIREMENTS[gridValue];
        }
        return super.getStructureRequirement(pos);
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (block == MekBeyondBlocks.ADVANCED_FUSION_CASING.get()) {
            return FormationProtocol.CasingType.FRAME; // 基本構造
        } else if (block == MekBeyondBlocks.ADVANCED_FUSION_PORT.get()) {
            return FormationProtocol.CasingType.VALVE; // 搬入出
        } else if (block == GeneratorsBlocks.REACTOR_GLASS.get() ||
                block == GeneratorsBlocks.LASER_FOCUS_MATRIX.get()) {
            return FormationProtocol.CasingType.OTHER; // ガラス (ALLOWED_GRIDの '2' の場所のみ許可される)
        }
        return FormationProtocol.CasingType.INVALID;
    }

    @Override
    protected boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        // 中央座標の計算 (7x7x7なので中心は min+3)
        int centerX = cuboid.getMinPos().getX() + 3;
        int centerZ = cuboid.getMinPos().getZ() + 3;

        // 一番真ん中の一直線（XZが中心）の判定
        if (pos.getX() == centerX && pos.getZ() == centerZ) {
            // この場所は空気ではなく「Advanced Fusion Casing」でなければならない
            if (state.getBlock() == MekBeyondBlocks.ADVANCED_FUSION_CASING.get()) {
                return true;
            }
            // ガラスや空気、ポートが真ん中の柱にある場合は失敗
            return false;
        }

        // 柱以外の内部空間は空気でなければならない
        return state.isAir();
    }

    @Override
    public boolean precheck() {
        // 指定サイズ(7x7x7)の直方体を探す
        this.cuboid = StructureHelper.fetchCuboid(this.structure, BOUNDS, BOUNDS, EnumSet.allOf(VoxelCuboid.CuboidSide.class), 72);
        return this.cuboid != null;
    }

    @Override
    public FormationProtocol.FormationResult postcheck(AdvancedFusionReactorMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        // 構造形成後の最終チェック
        // 特に特殊な条件がなければ成功を返す
        return FormationProtocol.FormationResult.SUCCESS;
    }
}
