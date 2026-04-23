package com.github.kousaba.mekanism_beyond.client.render;


import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.AdvancedFusionReactorMultiblockData;
import com.github.kousaba.mekanism_beyond.multiblock.advanced_fusion_reactor.TileEntityAdvancedFusionCasing;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class RenderAdvancedFusionReactor extends MekanismTileEntityRenderer<TileEntityAdvancedFusionCasing> {

    private static final Color CHERENKOV_BLUE = Color.rgbad(0.1, 0.4, 1.0, 0.8);
    private final BoltRenderer boltRenderer = new BoltRenderer();

    public RenderAdvancedFusionReactor(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(TileEntityAdvancedFusionCasing tile, float partialTicks, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        AdvancedFusionReactorMultiblockData multiblock = tile.getMultiblock();

        if (!multiblock.isFormed() || !multiblock.isBurning()) return;

        Vec3 center = Vec3.atLowerCornerOf(multiblock.getMinPos()).add(3.5, 3.5, 3.5);

        matrix.pushPose();
        matrix.translate(center.x - tile.getBlockPos().getX(), center.y - tile.getBlockPos().getY(), center.z - tile.getBlockPos().getZ());

        // --- 速度計算のロジック ---
        int injectionRate = multiblock.getInjectionRate();
        // レート2を基準(1.0)として、平方根で倍率を計算する
        // 式: sqrt(rate / 2)
        // レート2 -> 1.0倍
        // レート98 -> 約7倍
        // レート200 -> 約10倍
        float speedFactor = (float) Math.sqrt(injectionRate / 2.0f);

        // 非常に大きな値になると計算精度が落ちるため剰余をとる
        float gameTime = (tile.getLevel().getGameTime() % 200000) + partialTicks;

        for (int i = 0; i < 3; i++) {
            // 計算した speedFactor を引数に追加
            renderOrbit(i, gameTime, speedFactor);
        }

        boltRenderer.render(partialTicks, matrix, renderer);
        matrix.popPose();
    }

    // 引数に speedFactor を追加
    private void renderOrbit(int orbitIndex, float time, float speedFactor) {
        float radius = 2.5f;
        int ringSegments = 12;

        // --- 縦と横の回転角度を両方動的に計算 ---
        // 縦方向(X軸)の回転速度: orbitIndexごとに少し変えて重なりを防ぐ
        float angleX = time * (0.8f + orbitIndex * 0.2f) * speedFactor;
        // 横方向(Y軸)の回転速度: 縦より少し速く設定
        float angleY = time * (1.5f + orbitIndex * 0.5f) * speedFactor;
        // 隠し味にZ軸(ひねり)も少し入れるとより複雑になります
        float angleZ = time * (0.3f + orbitIndex * 0.1f) * speedFactor;

        for (int s = 0; s < ringSegments; s++) {
            double deg1 = (360.0 / ringSegments) * s;
            double deg2 = (360.0 / ringSegments) * (s + 1);

            Vector3f startPos = new Vector3f(radius * Mth.cos((float) Math.toRadians(deg1)), radius * Mth.sin((float) Math.toRadians(deg1)), 0);
            Vector3f endPos = new Vector3f(radius * Mth.cos((float) Math.toRadians(deg2)), radius * Mth.sin((float) Math.toRadians(deg2)), 0);

            // 全ての軸に対して回転を適用 (ひねりを加えた3D回転)
            rotateVector(startPos, angleX, angleY, angleZ);
            rotateVector(endPos, angleX, angleY, angleZ);

            BoltEffect bolt = new BoltEffect(
                    BoltEffect.BoltRenderInfo.electricity().color(CHERENKOV_BLUE),
                    new Vec3(startPos),
                    new Vec3(endPos),
                    6
            );

            bolt.size(0.05f)
                    .count(1)
                    .lifespan(1)
                    .spawn(BoltEffect.SpawnFunction.NO_DELAY)
                    .fade(BoltEffect.FadeFunction.NONE);

            boltRenderer.update("orbit_" + orbitIndex + "_" + s, bolt, time);
        }
    }


    private void rotateVector(Vector3f vec, float angleX, float angleY, float angleZ) {
        vec.rotateX((float) Math.toRadians(angleX));
        vec.rotateY((float) Math.toRadians(angleY));
        vec.rotateZ((float) Math.toRadians(angleZ));
    }

    @Override
    protected String getProfilerSection() {
        return MekanismBeyond.MODID + ":advanced_fusion_reactor";

    }

    private void rotateVector(Vector3f vec, float angleX, float angleY) {
        vec.rotateX((float) Math.toRadians(angleX));
        vec.rotateY((float) Math.toRadians(angleY));
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityAdvancedFusionCasing tile) {
        return true;
    }
}