package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public final class Continentalness {

    public static final Climate.Parameter MUSHROOM_FIELDS =
            Climate.Parameter.span(-1.2F, -1.05F);
    public static final Climate.Parameter DEEP_OCEAN =
            Climate.Parameter.span(-1.05F, -0.455F);
    public static final Climate.Parameter OCEAN =
            Climate.Parameter.span(-0.455F, -0.19F);
    public static final Climate.Parameter COAST =
            Climate.Parameter.span(-0.19F, -0.11F);
    public static final Climate.Parameter INLAND =
            Climate.Parameter.span(-0.11F, 0.55F);
    public static final Climate.Parameter NEAR_INLAND =
            Climate.Parameter.span(-0.11F, 0.03F);
    public static final Climate.Parameter MID_INLAND =
            Climate.Parameter.span(0.03F, 0.3F);
    public static final Climate.Parameter FAR_INLAND =
            Climate.Parameter.span(0.3F, 1.0F);
    public static final Climate.Parameter FULL_RANGE =
            Climate.Parameter.span(-1.0F, 1.0F);
}
