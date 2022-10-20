package net.frozenblock.lib.worldgen.biome.api;

import net.frozenblock.lib.worldgen.biome.impl.OverworldBiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

/**
 * API that exposes the internals of Minecraft's overworld biome code.
 */
public final class FrozenOverworldBiomes {
    private FrozenOverworldBiomes() {
		throw new UnsupportedOperationException("FrozenOverworldBiomes only contains static declarations.");
    }

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome			The biome to add. Must not be null.
	 * @param targetPoint	data about the given {@link Biome}'s spawning information in the Overworld.
	 * @see Climate.TargetPoint
	 */
    public static void addOverworldBiome(ResourceKey<Biome> biome, Climate.TargetPoint targetPoint) {
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

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome				The {@link Biome} to add. Must not be null.
	 * @param parameterPoint	data about the given {@link Biome}'s spawning information in the Overworld.
	 * @see Climate.ParameterPoint
	 */
    public static void addOverworldBiome(ResourceKey<Biome> biome, Climate.ParameterPoint parameterPoint) {
        OverworldBiomeData.addOverworldBiome(biome, parameterPoint);
    }

	/**
	 * Returns true if the given biome can generate in the Overworld, considering the Vanilla Overworld biomes,
	 * and any biomes added to the Overworld by mods.
	 */
    public static boolean canGenerateInOverworld(ResourceKey<Biome> biome) {
        return OverworldBiomeData.canGenerateInOverworld(biome);
    }
}
