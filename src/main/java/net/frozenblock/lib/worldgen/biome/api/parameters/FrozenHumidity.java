package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public class FrozenHumidity {

    public static final Climate.Parameter[] humidities = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0F, -0.35F),
            Climate.Parameter.span(-0.35F, -0.1F),
            Climate.Parameter.span(-0.1F, 0.1F),
            Climate.Parameter.span(0.1F, 0.3F),
            Climate.Parameter.span(0.3F, 1.0F)
    };

    public static final Climate.Parameter ARID = humidities[0];
    public static final Climate.Parameter DRY = humidities[1];
    public static final Climate.Parameter NEUTRAL = humidities[2];
    public static final Climate.Parameter WET = humidities[3];
    public static final Climate.Parameter HUMID = humidities[4];
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
}
