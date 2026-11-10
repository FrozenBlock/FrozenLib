package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record InBiomes(HolderSet<Biome> biomes) implements BiomeSelector {
	public static final MapCodec<InBiomes> CODEC = RegistryCodecs.holderSet(Registries.BIOME).fieldOf("biomes")
		.xmap(InBiomes::new, InBiomes::biomes);

	@Override
	public boolean test(BiomeSelectionContext context) {
		return context.inHolderSet(this.biomes);
	}

	@Override
	public void validate(ValidationContext context) {}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}
