package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.Structure;
import mekanism.common.lib.multiblock.StructureHelper;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PBFusionValidator extends CuboidStructureValidator<PBFusionReactorMultiblockData> {
    private static final VoxelCuboid BOUNDS = new VoxelCuboid(11, 11, 11);

    // 11x11の壁面パターン (0:無視/設置不可, 1:フレーム, 2:フレームまたはガラス)
    private static final byte[][] ALLOWED_GRID = new byte[][]{
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

    // 重複を避けるため、バリデーションごとにクリアされるリスト
    private final List<BlockPos> foundCoils = new ArrayList<>();

    @Override
    protected FormationProtocol.StructureRequirement getStructureRequirement(BlockPos pos) {
        VoxelCuboid.WallRelative relative = this.cuboid.getWallRelative(pos);
        if (relative.isWall()) {
            Structure.Axis axis = Structure.Axis.get(this.cuboid.getSide(pos));
            BlockPos relativePos = pos.subtract(this.cuboid.getMinPos());
            int h = axis.horizontal().getCoord(relativePos);
            int v = axis.vertical().getCoord(relativePos);
            // ALLOWED_GRID が 0 の場所は IGNORED (空気等でもOK) にする
            byte type = ALLOWED_GRID[h][v];
            if (type == 0) {
                return FormationProtocol.StructureRequirement.IGNORED;
            }
            return FormationProtocol.StructureRequirement.REQUIREMENTS[type];
        }
        return super.getStructureRequirement(pos);
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (block == MekBeyondBlocks.BEYOND_FUSION_CASING.get()) {
            return FormationProtocol.CasingType.FRAME;
        } else if (block == MekBeyondBlocks.BEYOND_FUSION_PORT.get()) {
            return FormationProtocol.CasingType.VALVE;
        } else if (block == GeneratorsBlocks.REACTOR_GLASS.get()) {
            return FormationProtocol.CasingType.OTHER;
        }
        return FormationProtocol.CasingType.INVALID;
    }

    @Override
    public boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        BlockPos relPos = pos.subtract(cuboid.getMinPos());
        int x = relPos.getX();
        int y = relPos.getY();
        int z = relPos.getZ();
        Block block = state.getBlock();

        // 1. 8つの内部の隅 (八角形の角を埋めるCasing)
        boolean isCorner = (x == 1 || x == 9) && (y == 1 || y == 9) && (z == 1 || z == 9);
        if (isCorner) {
            return block == MekBeyondBlocks.BEYOND_FUSION_CASING.get();
        }

        // 2. 中心の 3x3 磁場安定化コイルの柱
        if (x >= 4 && x <= 6 && z >= 4 && z <= 6 && y >= 1 && y <= 9) {
            return block == MekBeyondBlocks.MAGNETIC_STABILIZATION_COIL.get();
        }

        // 3. 4箇所の Supercharged Coil (東西南北の内側)
        boolean isCoilPos = (y == 5) && (
                (x == 5 && z == 1) || (x == 5 && z == 9) || (x == 1 && z == 5) || (x == 9 && z == 5)
        );

        if (isCoilPos) {
            if (block == MekanismBlocks.SUPERCHARGED_COIL.get()) {
                BlockPos immutablePos = pos.immutable();
                // 重複してリストに追加されないようにチェック
                if (!foundCoils.contains(immutablePos)) {
                    foundCoils.add(immutablePos);
                }
                return true;
            }
            return false;
        }

        // 4. それ以外は空気
        return state.isAir();
    }

    @Override
    public boolean precheck() {
        foundCoils.clear(); // 判定前にリストをリセット
        // 11x11x11 の cuboid を探す。
        // 第5引数(minBridge)は、八角形のようにフレームが断続的な場合、小さめの値（例: 50）にすると安定します。
        this.cuboid = StructureHelper.fetchCuboid(this.structure, BOUNDS, BOUNDS, EnumSet.allOf(VoxelCuboid.CuboidSide.class), 200);
        return this.cuboid != null;
    }

    @Override
    public FormationProtocol.FormationResult postcheck(PBFusionReactorMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        if (foundCoils.size() != 4) {
            return FormationProtocol.FormationResult.fail(Component.literal("東西南北の壁の内側(5,5)にSupercharged Coilを計4つ設置してください。見つかった数: " + foundCoils.size()));
        }
        // レンダリング用に保存
        structure.superchargedCoils = new ArrayList<>(foundCoils);
        return FormationProtocol.FormationResult.SUCCESS;
    }
}
