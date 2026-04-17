package com.github.kousaba.mekanism_beyond.multiblock.transmuter;

import com.github.kousaba.mekanism_beyond.registration.MekBeyondBlocks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.StructureHelper;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.EnumSet;

public class TransmuterValidator extends CuboidStructureValidator<TransmuterMultiblockData> {
    private static final VoxelCuboid MIN_BOUNDS = new VoxelCuboid(5, 7, 5);
    private static final VoxelCuboid MAX_BOUNDS = new VoxelCuboid(17, 18, 17);
    private int electromagneticCoils = 0;
    private boolean superchargedCoilFound = false;
    private boolean innerStructureValid = true;

    public TransmuterValidator() {
        super();
        System.out.println("Validator Constructor");
    }

    @Override
    protected FormationProtocol.CasingType getCasingType(BlockState state) {
        Block block = state.getBlock();
        if (block == MekBeyondBlocks.TRANSMUTER_CASING.get()) {
            return FormationProtocol.CasingType.FRAME;
        } else if (block == MekBeyondBlocks.TRANSMUTER_PORT.get()) { // ポート用ブロック
            return FormationProtocol.CasingType.VALVE;
        } else if (block == MekanismBlocks.STRUCTURAL_GLASS.get()){
            return FormationProtocol.CasingType.OTHER;
        }
        return FormationProtocol.CasingType.INVALID;
    }


    @Override
    public boolean validateInner(BlockState state, Long2ObjectMap<ChunkAccess> chunkMap, BlockPos pos) {
        if (super.validateInner(state, chunkMap, pos)) {
            return true;
        }

        int minH = cuboid.getMinPos().getY() + 1; // 内部の一番下
        int maxH = cuboid.getMaxPos().getY() - 1; // 内部の一番上

        int centerX = (cuboid.getMinPos().getX() + cuboid.getMaxPos().getX()) / 2;
        int centerZ = (cuboid.getMinPos().getZ() + cuboid.getMaxPos().getZ()) / 2;

        Block block = state.getBlock();

        // --- 1. 空気層（一番上と一番下 "以外" のすべての層）のチェック ---
        if (pos.getY() > minH && pos.getY() < maxH) {
            if (!state.isAir()) {
                System.out.println("[TransmuterValidator] 内部検証失敗 座標:" + pos.toShortString() + " | 原因: 空気層に " + BuiltInRegistries.BLOCK.getKey(block) + " がありました。");
                innerStructureValid = false;
                return false;
            }
            return true; // 空気ならOK
        }

        // --- 2. コイル層（一番上 minH と一番下 maxH）のチェック ---
        if (pos.getY() == minH || pos.getY() == maxH) {

            // ① 中心軸のみ Supercharged Coil または Electromagnetic Coil を許可
            if (pos.getX() == centerX && pos.getZ() == centerZ) {
                if (block == MekanismBlocks.SUPERCHARGED_COIL.get()) {
                    superchargedCoilFound = true;
                    return true;
                } else if (block == GeneratorsBlocks.ELECTROMAGNETIC_COIL.get()) {
                    electromagneticCoils++;
                    return true;
                }
            }
            // ② 中心軸以外のコイル層全体は Electromagnetic Coil を許可
            else if (block == GeneratorsBlocks.ELECTROMAGNETIC_COIL.get()) {
                electromagneticCoils++;
                return true;
            }

            // コイル以外のブロックが置かれていたら失敗
            if (!state.isAir()) {
                System.out.println("[TransmuterValidator] 内部検証失敗 座標:" + pos.toShortString() + " | 原因: コイル層にコイル以外のブロック: " + BuiltInRegistries.BLOCK.getKey(block));
                return false;
            }

            // コイル層に空気を置くのは許可（ぎっしり敷き詰めなくてもOKな場合）
            return true;
        }

        return true;
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
        electromagneticCoils = 0;
        superchargedCoilFound = false;
        innerStructureValid = true;
        cuboid = StructureHelper.fetchCuboid(structure, MIN_BOUNDS, MAX_BOUNDS, EnumSet.allOf(VoxelCuboid.CuboidSide.class), 500);
        if (cuboid == null){
            System.out.println("[TransmuterValidator] precheck() 失敗: 直方体(Cuboid)が形成できませんでした。枠組みが閉じていないか、サイズが範囲外(5x7x5 ～ 17x18x17)です。");
            return false;
        }
        System.out.println("[TransmuterValidator] precheck() 成功: 枠のサイズは " + cuboid.length() + "x" + cuboid.height() + "x" + cuboid.width() + " です。");
        return true;
    }
}