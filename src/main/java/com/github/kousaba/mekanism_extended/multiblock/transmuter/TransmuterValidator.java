package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import com.github.kousaba.mekanism_extended.registration.ModBlocks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.StructureHelper;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.EnumSet;


public class TransmuterValidator extends CuboidStructureValidator<TransmuterMultiblockData>{
    private static final VoxelCuboid MIN_BOUNDS = new VoxelCuboid(1,1,1);
    private static final VoxelCuboid MAX_BOUNDS = new VoxelCuboid(17,18,17);
    private int electromagneticCoils = 0;
    private boolean superchargedCoilFound = false;
    private boolean innerStructureValid = true;

    public TransmuterValidator(){
        System.out.println("Validator Constructor");
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        System.out.println("getCasingType called");
        if (block == ModBlocks.TRANSMUTER_CASING.get()) {
            return FormationProtocol.CasingType.FRAME;
        } else if (block == ModBlocks.TRANSMUTER_PORT.get()) { // ポート用ブロック
            return FormationProtocol.CasingType.VALVE;
        }
        return FormationProtocol.CasingType.INVALID;
    }

    @Override
    public boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        System.out.println("validateInner");
        if (super.validateInner(state, chunkMap, pos)) {
            return true;
        }

        // 内部座標の特定（中心位置の計算用）
        int minH = cuboid.getMinPos().getY() + 1;
        int maxH = cuboid.getMaxPos().getY() - 1;
        int midY = (minH + maxH) / 2;
        int centerX = (cuboid.getMinPos().getX() + cuboid.getMaxPos().getX()) / 2;
        int centerZ = (cuboid.getMinPos().getZ() + cuboid.getMinPos().getZ()) / 2;

        // --- 1. 中央3層は空気でなければならない ---
        if (pos.getY() >= midY - 1 && pos.getY() <= midY + 1) {
            if (!state.isAir()) {
                innerStructureValid = false;
                return false;
            }
            return true;
        }

        // --- 2. コイル層（中央3層以外）のチェック ---
        // 中心軸：Supercharged Coilを許可
        if (pos.getX() == centerX && pos.getZ() == centerZ) {
            if (state.is(MekanismBlocks.SUPERCHARGED_COIL)) {
                superchargedCoilFound = true;
                return true;
            }
        }
        // 中心3x3：Electromagnetic Coilを許可（または空気も許可）
        else if (Math.abs(pos.getX() - centerX) <= 1 && Math.abs(pos.getZ() - centerZ) <= 1) {
            if (state.is(GeneratorsBlocks.ELECTROMAGNETIC_COIL)) {
                electromagneticCoils++;
                return true;
            }
        }

        // それ以外の内部は空気であることを許可
        return state.isAir();
    }

    @Override
    public FormationProtocol.FormationResult postcheck(TransmuterMultiblockData structure, Long2ObjectMap<ChunkAccess> chunkMap) {
        System.out.println("postcheck");
        // 中央3層にブロックが置かれていた場合
        if (!innerStructureValid) {
            return FormationProtocol.FormationResult.fail(Component.literal("中央の3層は空気である必要があります。"));
        }

        // Supercharged Coilが1つも見つからなかった場合
        if (!superchargedCoilFound) {
            return FormationProtocol.FormationResult.fail(Component.literal("中心軸にSupercharged Coilを設置してください。"));
        }

        // 構造が有効な場合、Data側にスキャン結果を反映
        structure.setCoilData(superchargedCoilFound, electromagneticCoils);

        return FormationProtocol.FormationResult.SUCCESS;
    }

    @Override
    public boolean precheck() {
        System.out.println("precheck");
        // fetchCuboidの引数で最小・最大サイズを指定
        cuboid = StructureHelper.fetchCuboid(structure, MIN_BOUNDS, MAX_BOUNDS, EnumSet.allOf(VoxelCuboid.CuboidSide.class), 500);
        return cuboid != null;
    }
}