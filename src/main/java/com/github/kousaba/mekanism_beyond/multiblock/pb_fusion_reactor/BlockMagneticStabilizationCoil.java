package com.github.kousaba.mekanism_beyond.multiblock.pb_fusion_reactor;

import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockMagneticStabilizationCoil extends Block implements IHasDescription {
    public BlockMagneticStabilizationCoil(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> "description.mekanism_beyond.magnetic_stabilization_coil";
    }
}
