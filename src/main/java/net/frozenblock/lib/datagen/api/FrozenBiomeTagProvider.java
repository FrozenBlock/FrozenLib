package net.frozenblock.lib.datagen.api;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

public abstract class FrozenBiomeTagProvider extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
	public FrozenBiomeTagProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator, Registry.BIOME_REGISTRY);
	}
}
