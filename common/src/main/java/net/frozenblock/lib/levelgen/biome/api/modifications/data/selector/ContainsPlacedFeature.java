package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record ContainsPlacedFeature(ResourceKey<PlacedFeature> placedFeature) implements BiomeSelector {
	public static final MapCodec<ContainsPlacedFeature> CODEC = ResourceKey.codec(Registries.PLACED_FEATURE).fieldOf("placed_feature")
		.xmap(ContainsPlacedFeature::new, ContainsPlacedFeature::placedFeature);

	@Override
	public boolean test(BiomeSelectionContext context) {
		return context.hasPlacedFeature(this.placedFeature);
	}

	@Override
	public void validate(ValidationContext context) {}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}
