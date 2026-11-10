package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record ContainsFeature(ResourceKey<Feature> feature) implements BiomeSelector {
	public static final MapCodec<ContainsFeature> CODEC = ResourceKey.codec(Registries.FEATURE).fieldOf("feature")
		.xmap(ContainsFeature::new, ContainsFeature::feature);

	@Override
	public boolean test(BiomeSelectionContext context) {
		return context.hasFeature(this.feature);
	}

	@Override
	public void validate(ValidationContext context) {}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}
