package net.frozenblock.lib.levelgen.biome.impl.modifications;

import net.frozenblock.lib.levelgen.biome.impl.FrozenGrassColorModifier;
import net.minecraft.world.level.biome.Biome;

public interface BiomeInterface {
	Biome.ClimateSettings frozenLib$getClimateSettings();
	void frozenLib$setClimateSettings(Biome.ClimateSettings settings);

	void frozenLib$setFrozenGrassColorModifier(FrozenGrassColorModifier frozenGrassColorModifier);
	FrozenGrassColorModifier frozenLib$getFrozenGrassColorModifier();
}
