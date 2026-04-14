package com.github.kousaba.mekanism_extended.multiblock.transmuter;

import mekanism.common.lib.multiblock.MultiblockManager;

public class TransmuterManager extends MultiblockManager<TransmuterMultiblockData> {

    // 全体のインスタンスを定義（これをTileEntityから参照します）
    public static final TransmuterManager INSTANCE = new TransmuterManager();

    private TransmuterManager() {
        // 第一引数は、セーブデータ(NBT)内での識別名
        // 第二引数は、Cacheを作成するためのサプライヤー
        super("transmuter", TransmuterCache::new, TransmuterValidator::new);
    }
}