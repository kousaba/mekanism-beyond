package com.github.kousaba.mekanism_beyond.client.render;


import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TileEntityTransmuterCasing;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterMultiblockData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.data.FluidRenderData;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.client.render.tileentity.MultiblockTileEntityRenderer;
import mekanism.common.lib.effect.BoltEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NothingNullByDefault
public class RenderTransmuter extends MultiblockTileEntityRenderer<TransmuterMultiblockData, TileEntityTransmuterCasing> {

    private static final Map<UUID, BoltRenderer> boltRendererMap = new HashMap<>();

    public RenderTransmuter(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(TileEntityTransmuterCasing tile, TransmuterMultiblockData multiblock, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        if (!multiblock.isFormed() || multiblock.getBounds() == null) return;

        // --- 1. 流体(水)のレンダリング ---
        if (!multiblock.waterTank.isEmpty()) {
            VertexConsumer buffer = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            // 内部の空気層の底から、現在の量に応じた高さまでレンダリング
            // 内部高さ(height-2)のうち、水が満たされる
            FluidRenderData data = FluidRenderData.Builder.create(multiblock.waterTank.getFluid())
                    .of(multiblock)
                    .height(multiblock.height() - 2)
                    .build();

            // prevScaleを使用して水位を滑らかに表示
            matrix.pushPose();
            // Casingの位置からの相対座標で描画
            renderObject(data, multiblock.valves, tile.getBlockPos(), matrix, buffer, overlayLight, multiblock.prevScale);
            matrix.popPose();
        }

        // --- 2. レーザー(電磁ボルト)のレンダリング ---
        if (multiblock.isActive()) {
            BoltRenderer bolts = boltRendererMap.computeIfAbsent(multiblock.inventoryID, (id) -> new BoltRenderer());

            // マルチブロックの中心を計算
            Vec3 center = Vec3.atLowerCornerOf(multiblock.getMinPos())
                    .add(Vec3.atLowerCornerOf(multiblock.getMaxPos()))
                    .add(1, 1, 1).scale(0.5);

            Vec3 renderCenter = center.subtract(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());

            // 各Supercharged Coilから中心へボルトを飛ばす
            for (BlockPos coilPos : multiblock.superchargedCoils) {
                // コイルから中心へのベクトル
                Vec3 start = Vec3.atCenterOf(coilPos).subtract(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());

                BoltEffect bolt = new BoltEffect(BoltEffect.BoltRenderInfo.ELECTRICITY, start, renderCenter, 10)
                        .count(1)
                        .size(0.02F)
                        .lifespan(4)
                        .spawn(BoltEffect.SpawnFunction.NO_DELAY);

                bolts.update(coilPos.hashCode(), bolt, partialTick);
            }
            bolts.render(partialTick, matrix, renderer);
        }
    }

    @Override
    protected boolean shouldRender(TileEntityTransmuterCasing tile, TransmuterMultiblockData multiblock, Vec3 camera) {
        return super.shouldRender(tile, multiblock, camera) && multiblock.isFormed();
    }

    @Override
    protected String getProfilerSection() {
        return "transmuter";
    }
}