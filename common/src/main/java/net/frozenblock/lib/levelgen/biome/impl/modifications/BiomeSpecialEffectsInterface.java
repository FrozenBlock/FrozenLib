package net.frozenblock.lib.levelgen.biome.impl.modifications;

import net.minecraft.world.level.biome.BiomeSpecialEffects;
import java.util.Optional;

public interface BiomeSpecialEffectsInterface {

	void frozenLib$setWaterColor(int color);

	void frozenLib$setFoliageColorOverride(Optional<Integer> color);

	void frozenLib$setDryFoliageColorOverride(Optional<Integer> color);

	void frozenLib$setGrassColorOverride(Optional<Integer> color);

	void frozenLib$setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier colorModifier);
}
