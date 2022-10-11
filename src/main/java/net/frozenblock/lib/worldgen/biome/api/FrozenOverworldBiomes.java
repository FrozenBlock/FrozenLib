package net.frozenblock.lib.worldgen.biome.api;

import net.frozenblock.lib.worldgen.biome.impl.OverworldBiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

public final class FrozenOverworldBiomes {
    private FrozenOverworldBiomes() {
    }

    public static void addOverworldBiome(ResourceKey<Biome> biome,
                                         Climate.TargetPoint targetPoint) {
        OverworldBiomeData.addOverworldBiome(biome, Climate.parameters(
                targetPoint.temperature(),
                targetPoint.humidity(),
                targetPoint.continentalness(),
                targetPoint.erosion(),
                targetPoint.depth(),
                targetPoint.weirdness(),
                0
        ));
    }

    public static void addOverworldBiome(ResourceKey<Biome> biome,
                                         Climate.ParameterPoint parameterPoint) {
        OverworldBiomeData.addOverworldBiome(biome, parameterPoint);
    }

    public static boolean canGenerateInOverworld(ResourceKey<Biome> biome) {
        return OverworldBiomeData.canGenerateInOverworld(biome);
    }
}
