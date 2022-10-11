package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public final class Erosion {

    public static final Climate.Parameter[] erosions = new Climate.Parameter[] {
            Climate.Parameter.span(-1.0F, -0.78F),
            Climate.Parameter.span(-0.78F, -0.375F),
            Climate.Parameter.span(-0.375F, -0.2225F),
            Climate.Parameter.span(-0.2225F, 0.05F),
            Climate.Parameter.span(0.05F, 0.45F),
            Climate.Parameter.span(0.45F, 0.55F),
            Climate.Parameter.span(0.55F, 1.0F)
    };

    public static final Climate.Parameter EROSION_0 = erosions[0];
    public static final Climate.Parameter EROSION_1 = erosions[1];
    public static final Climate.Parameter EROSION_2 = erosions[2];
    public static final Climate.Parameter EROSION_3 = erosions[3];
    ;
    public static final Climate.Parameter EROSION_4 = erosions[4];
    public static final Climate.Parameter EROSION_5 = erosions[5];
    public static final Climate.Parameter EROSION_6 = erosions[6];
    public static final Climate.Parameter FULL_RANGE =
            Climate.Parameter.span(-1.0F, 1.0F);
}
