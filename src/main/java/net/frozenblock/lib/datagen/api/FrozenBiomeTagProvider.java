package net.frozenblock.lib.datagen.api;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

public abstract class FrozenBiomeTagProvider extends FabricTagProvider<Biome> {

	public FrozenBiomeTagProvider(FabricDataOutput output, CompletableFuture registriesFuture) {
		super(output, Registry.BIOME_REGISTRY, registriesFuture);
	}
}
