package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public final class Depth {

    public static final Climate.Parameter[] depths = new Climate.Parameter[] {
            Climate.Parameter.point(0.0F),
            Climate.Parameter.span(0.2F, 0.9F),
            Climate.Parameter.point(1.0F)
    };

    public static final Climate.Parameter SURFACE = depths[0];
    public static final Climate.Parameter UNDERGROUND = depths[1];
    public static final Climate.Parameter FLOOR = depths[2];
    public static final Climate.Parameter FULL_RANGE =
            Climate.Parameter.span(-1.0F, 1.0F);
}
