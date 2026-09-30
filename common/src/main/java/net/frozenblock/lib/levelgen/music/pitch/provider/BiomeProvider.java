package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.biome.Biome;

public record BiomeProvider(HolderSet<Biome> requiredBiomes, PitchProvider provider) implements PitchProvider {
	public static final MapCodec<BiomeProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.BIOME).fieldOf("required_biomes").forGetter(BiomeProvider::requiredBiomes),
		DIRECT_CODEC.fieldOf("provider").forGetter(BiomeProvider::provider)
	).apply(instance, BiomeProvider::new));

	@Override
	public float sample(long gameTime) {
		return this.provider.sample(gameTime);
	}

	@Override
	public boolean applicable(Context context) {
		return this.requiredBiomes.contains(context.biome()) && this.provider.applicable(context);
	}

	@Override
	public MapCodec<BiomeProvider> codec() {
		return CODEC;
	}
}
