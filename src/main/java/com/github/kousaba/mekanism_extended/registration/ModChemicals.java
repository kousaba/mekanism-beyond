package com.github.kousaba.mekanism_extended.registration;

import com.github.kousaba.mekanism_extended.MekanismExtended;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;

public class ModChemicals {
    public static final ChemicalDeferredRegister CHEMICALS = new ChemicalDeferredRegister(MekanismExtended.MODID);

    public static final DeferredChemical<Chemical> URANIUM_WATER = CHEMICALS.register("uranium_water", () -> new Chemical(ChemicalBuilder.builder().tint(0x4CAF50)));
}
