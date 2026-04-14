package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import mekanism.common.lib.multiblock.MultiblockCache;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class TransmuterCache extends MultiblockCache<TransmuterMultiblockData> {

    // 保存したい統計情報や状態
    private boolean hasSuperchargedCoil = false;
    private int electromagneticCoilCount = 0;
    private double currentProbability = 0;
    private long currentProductionRate = 0;

    @Override
    public void merge(MultiblockCache<TransmuterMultiblockData> mergeCache, RejectContents rejectContents) {
        super.merge(mergeCache, rejectContents);
        TransmuterCache other = (TransmuterCache) mergeCache;

        // データをマージ（結合）する際のロジック
        // コイルの有無などは論理和や最大値をとるのが一般的です
        this.hasSuperchargedCoil |= other.hasSuperchargedCoil;
        this.electromagneticCoilCount = Math.max(this.electromagneticCoilCount, other.electromagneticCoilCount);
        this.currentProbability = Math.max(this.currentProbability, other.currentProbability);
        this.currentProductionRate = Math.max(this.currentProductionRate, other.currentProductionRate);
    }

    @Override
    public void apply(HolderLookup.Provider provider, TransmuterMultiblockData data) {
        super.apply(provider, data);

        // キャッシュからマルチブロックの実体データへ値を適用
        data.setCoilData(hasSuperchargedCoil, electromagneticCoilCount);
        // 生成確率やレートは本来サイズから再計算されますが、キャッシュから戻すことも可能です
    }

    @Override
    public void sync(TransmuterMultiblockData data) {
        super.sync(data);

        // マルチブロックの実体データからキャッシュへ値をコピー
        this.hasSuperchargedCoil = data.hasSuperchargedCoil(); // Data側にgetterが必要
        this.electromagneticCoilCount = data.getCoilCount();
        this.currentProbability = data.getProbability();
        this.currentProductionRate = data.getProductionRate();
    }

    @Override
    public void load(HolderLookup.Provider provider, CompoundTag nbtTags) {
        super.load(provider, nbtTags);

        // NBTから読み込み（ワールド保存用）
        this.hasSuperchargedCoil = nbtTags.getBoolean("hasSupercharged");
        this.electromagneticCoilCount = nbtTags.getInt("coilCount");
        this.currentProbability = nbtTags.getDouble("probability");
        this.currentProductionRate = nbtTags.getLong("prodRate");
    }

    @Override
    public void save(HolderLookup.Provider provider, CompoundTag nbtTags) {
        super.save(provider, nbtTags);

        // NBTへ保存（ワールド保存用）
        nbtTags.putBoolean("hasSupercharged", hasSuperchargedCoil);
        nbtTags.putInt("coilCount", electromagneticCoilCount);
        nbtTags.putDouble("probability", currentProbability);
        nbtTags.putLong("prodRate", currentProductionRate);
    }
}