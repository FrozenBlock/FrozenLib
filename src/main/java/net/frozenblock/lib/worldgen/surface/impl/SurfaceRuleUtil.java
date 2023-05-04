package net.frozenblock.lib.worldgen.surface.impl;

import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class SurfaceRuleUtil {

    public static void injectSurfaceRules(NoiseGeneratorSettings settings, ResourceKey<DimensionType> dimension) {
        var inter = NoiseGeneratorInterface.class.cast(settings);

        var newRules = FrozenSurfaceRules.getSurfaceRules(dimension);
        if (newRules != null) {
            inter.overwriteSurfaceRules(newRules);
        }
    }
}