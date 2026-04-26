package com.github.kousaba.mekanism_beyond.registration;

import com.github.kousaba.mekanism_beyond.MekanismBeyond;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;

public class MekBeyondChemicals {
    public static final ChemicalDeferredRegister CHEMICALS = new ChemicalDeferredRegister(MekanismBeyond.MODID);

    public static final DeferredChemical<Chemical> URANIUM_WATER = CHEMICALS.register("uranium_water", () -> new Chemical(ChemicalBuilder.builder().tint(0x4CAF50)));
    public static final DeferredChemical<Chemical> NEUTRON = CHEMICALS.register("neutron", 0xEEEEEE);
    public static final DeferredChemical<Chemical> PROTON = CHEMICALS.register("proton", 0xFFFFFF); // 陽子: 白（高エネルギー）
    public static final DeferredChemical<Chemical> BORON11 = CHEMICALS.register("boron11", 0xFF00FF); // ホウ素11: マゼンタ
}
