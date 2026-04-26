package com.github.kousaba.mekanism_beyond.client.render;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.PBFusionReactorMultiblockData;
import com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor.TileEntityBeyondFusionCasing;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.client.render.tileentity.MultiblockTileEntityRenderer;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class RenderPBFusionReactor extends MultiblockTileEntityRenderer<PBFusionReactorMultiblockData, TileEntityBeyondFusionCasing> {

    private static final Color PB_MAGENTA = Color.rgbad(1.0, 0.0, 1.0, 0.8);
    private static final Color PB_CYAN = Color.rgbad(0.0, 1.0, 1.0, 0.8);
    private final BoltRenderer boltRenderer = new BoltRenderer();

    public RenderPBFusionReactor(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(TileEntityBeyondFusionCasing tile, PBFusionReactorMultiblockData multiblock, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        if (!multiblock.isBurning()) return;

        // --- 中心座標の取得 (VoxelCuboid から正確な中心を得る) ---
        VoxelCuboid bounds = multiblock.getBounds();
        if (bounds == null) return;
        Vec3 center = Vec3.atLowerCornerOf(bounds.getMinPos()).add(
                bounds.length() / 2.0,
                bounds.height() / 2.0,
                bounds.width() / 2.0
        );

        matrix.pushPose();

        // --- 座標の移動 ---
        // 描画の基準点を「マルチブロックの幾何学的な中心」に移動させます。
        // これにより、(0,0,0) を指定すれば炉のど真ん中に描画されます。
        matrix.translate(
                center.x - tile.getBlockPos().getX(),
                center.y - tile.getBlockPos().getY(),
                center.z - tile.getBlockPos().getZ()
        );

        float gameTime = (tile.getLevel().getGameTime() % 200000) + partialTick;

        // サーバーから同期された半径と進捗
        float currentRadius = (float) multiblock.getVisualRadius();
        float pulseProgress = 1.0f - (currentRadius / 5.0f);

        // 色の計算
        float colorT = (Mth.sin(gameTime * 0.05f) + 1.0f) / 2.0f;
        Color currentColor = lerpColor(PB_MAGENTA, PB_CYAN, colorT);

        // 1. 収縮する球体の描画 (中心 0,0,0 基準)
        if (currentRadius > 0.1f) {
            for (int i = 0; i < 3; i++) {
                renderPulsingRing(i, currentRadius, gameTime, currentColor, pulseProgress);
            }
        }

        // 2. Supercharged Coil から中心(0,0,0)への電撃
        if (multiblock.superchargedCoils != null) {
            for (BlockPos coilPos : multiblock.superchargedCoils) {
                // atCenterOf(coilPos) = ブロックの(0.5, 0.5, 0.5)地点。
                // そこからマルチブロックの中心(center)を引くことで、中心を(0,0,0)とした相対座標が得られます。
                Vec3 startPosRelative = Vec3.atCenterOf(coilPos).subtract(center);

                float boltSize = 0.06f * (1.1f - pulseProgress);
                BoltEffect laser = new BoltEffect(
                        BoltEffect.BoltRenderInfo.electricity().color(currentColor),
                        startPosRelative,
                        Vec3.ZERO, // 行き先は常に炉の中心
                        10
                ).size(boltSize).lifespan(2).spawn(BoltEffect.SpawnFunction.NO_DELAY).fade(BoltEffect.FadeFunction.NONE);

                boltRenderer.update("pb_laser_" + coilPos.toShortString(), laser, gameTime);
            }
        }

        // 実際の描画
        boltRenderer.render(partialTick, matrix, renderer);
        matrix.popPose();
    }

    private void renderPulsingRing(int axisIndex, float radius, float time, Color color, float pulseProgress) {
        int segments = 12;
        float rotationSpeed = time * (2.0f + pulseProgress * 5.0f);

        for (int s = 0; s < segments; s++) {
            double deg1 = (360.0 / segments) * s;
            double deg2 = (360.0 / segments) * (s + 1);

            // 中心 (0,0,0) を基準とした座標
            Vector3f startPos = new Vector3f(radius * Mth.cos((float) Math.toRadians(deg1)), radius * Mth.sin((float) Math.toRadians(deg1)), 0);
            Vector3f endPos = new Vector3f(radius * Mth.cos((float) Math.toRadians(deg2)), radius * Mth.sin((float) Math.toRadians(deg2)), 0);

            float angleX = (axisIndex == 0) ? rotationSpeed : (axisIndex * 60f);
            float angleY = (axisIndex == 1) ? rotationSpeed : (axisIndex * 60f);
            float angleZ = (axisIndex == 2) ? rotationSpeed : (axisIndex * 60f);

            rotateVector(startPos, angleX, angleY, angleZ);
            rotateVector(endPos, angleX, angleY, angleZ);

            BoltEffect bolt = new BoltEffect(
                    BoltEffect.BoltRenderInfo.electricity().color(color),
                    new Vec3(startPos),
                    new Vec3(endPos),
                    6
            ).size(0.05f * (1.0f - pulseProgress)).lifespan(1).spawn(BoltEffect.SpawnFunction.NO_DELAY).fade(BoltEffect.FadeFunction.NONE);

            boltRenderer.update("pb_ring_" + axisIndex + "_" + s, bolt, time);
        }
    }

    private Color lerpColor(Color a, Color b, float t) {
        return Color.rgbad(
                Mth.lerp(t, a.rf(), b.rf()),
                Mth.lerp(t, a.gf(), b.gf()),
                Mth.lerp(t, a.bf(), b.bf()),
                Mth.lerp(t, a.af(), b.af())
        );
    }

    private void rotateVector(Vector3f vec, float angleX, float angleY, float angleZ) {
        if (angleX != 0) vec.rotateX((float) Math.toRadians(angleX));
        if (angleY != 0) vec.rotateY((float) Math.toRadians(angleY));
        if (angleZ != 0) vec.rotateZ((float) Math.toRadians(angleZ));
    }

    @Override
    protected String getProfilerSection() {
        return MekanismBeyond.MODID + ":pb_fusion_reactor";
    }
}
