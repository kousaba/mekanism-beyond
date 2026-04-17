package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import com.github.kousaba.mekanism_beyond.beyond_energy.BeyondFEWrapper;
import com.github.kousaba.mekanism_beyond.beyond_energy.IBeyondEnergyContainer;
import com.github.kousaba.mekanism_beyond.multiblock.transmuter.TransmuterMultiblockData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MekanismBeyond.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MekBeyondCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Transmuter Port に対して FE（EnergyStorage）の機能を登録する
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK, // FEのCapability
            MekBeyondTileEntities.TRANSMUTER_PORT.get(), // 対象のBlockEntity
            (tile, side) -> {
                TransmuterMultiblockData multiblock = tile.getMultiblock();
                if (multiblock.isFormed() && multiblock.energyContainer instanceof IBeyondEnergyContainer bec) {
                    // tile.getActive() が true なら Outputモード、false なら Inputモード
                    boolean isInputMode = !tile.getActive();
                    return new BeyondFEWrapper(bec, isInputMode);
                }
                return null; // マルチブロックが未完成の時は何もしない
            }
        );
    }
}
