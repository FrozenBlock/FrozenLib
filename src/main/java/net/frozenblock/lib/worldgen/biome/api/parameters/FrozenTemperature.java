package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public class FrozenTemperature {

    public static final Climate.Parameter[] temperatures = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0F, -0.45F),
            Climate.Parameter.span(-0.45F, -0.15F),
            Climate.Parameter.span(-0.15F, 0.2F),
            Climate.Parameter.span(0.2F, 0.55F),
            Climate.Parameter.span(0.55F, 1.0F)
    };

    public static final Climate.Parameter ICY = temperatures[0];
    public static final Climate.Parameter COOL = temperatures[1];
    public static final Climate.Parameter NEUTRAL = temperatures[2];
    public static final Climate.Parameter WARM = temperatures[3];
    public static final Climate.Parameter HOT = temperatures[4];
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
}
