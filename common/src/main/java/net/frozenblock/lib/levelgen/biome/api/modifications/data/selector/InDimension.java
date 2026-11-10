package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record InDimension(ResourceKey<LevelStem> dimension) implements BiomeSelector {
	public static final MapCodec<InDimension> CODEC = ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension")
		.xmap(InDimension::new, InDimension::dimension);

	@Override
	public boolean test(BiomeSelectionContext context) {
		return context.canGenerateIn(this.dimension);
	}

	@Override
	public void validate(ValidationContext context) {}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}
