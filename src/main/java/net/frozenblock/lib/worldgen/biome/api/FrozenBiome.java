package net.frozenblock.lib.worldgen.biome.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FrozenBiome {
	private final ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, new ResourceLocation(this.modID(), this.biomeID()));

    public abstract String modID();

	public abstract String biomeID();

	public abstract float temperature();

	public abstract float downfall();

	public boolean hasPrecipitation() {
		return true;
	}

	public Biome.TemperatureModifier temperatureModifier() {
		return Biome.TemperatureModifier.NONE;
	}

	public abstract int skyColor();

	public abstract int fogColor();

	public abstract int waterColor();

	public abstract int waterFogColor();

	@Nullable
	public abstract Integer foliageColorOverride();

	@Nullable
	public abstract Integer grassColorOverride();

	public BiomeSpecialEffects.GrassColorModifier grassColorModifier() {
		return BiomeSpecialEffects.GrassColorModifier.NONE;
	}

	@Nullable
	public abstract AmbientParticleSettings ambientParticleSettings();

	@Nullable
	public abstract Holder<SoundEvent> ambientLoopSound();

	@Nullable
	public abstract AmbientMoodSettings ambientMoodSettings();

	@Nullable
	public abstract AmbientAdditionsSettings ambientAdditionsSound();

	@Nullable
	public abstract Music backgroundMusic();

	public final @NotNull Biome create(@NotNull BootstapContext<Biome> entries) {
		Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder();
		biomeBuilder.temperature(this.temperature())
			.temperatureAdjustment(this.temperatureModifier())
			.downfall(this.downfall())
			.hasPrecipitation(this.hasPrecipitation());

		var placedFeatures = entries.lookup(Registries.PLACED_FEATURE);
		var worldCarvers = entries.lookup(Registries.CONFIGURED_CARVER);
		BiomeGenerationSettings.Builder featureBuilder = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);
		this.addFeatures(featureBuilder);
		biomeBuilder.generationSettings(featureBuilder.build());

		MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
		this.addSpawns(spawnBuilder);
		biomeBuilder.mobSpawnSettings(spawnBuilder.build());

		BiomeSpecialEffects.Builder specialEffectsBuilder = new BiomeSpecialEffects.Builder();
		specialEffectsBuilder.skyColor(this.skyColor())
			.fogColor(this.fogColor())
			.waterColor(this.waterColor())
			.waterFogColor(this.waterFogColor())
			.grassColorModifier(this.grassColorModifier());

		if (this.foliageColorOverride() != null) specialEffectsBuilder.foliageColorOverride(this.foliageColorOverride());
		if (this.grassColorOverride() != null) specialEffectsBuilder.grassColorOverride(this.grassColorOverride());
		if (this.ambientParticleSettings() != null) specialEffectsBuilder.ambientParticle(this.ambientParticleSettings());
		if (this.ambientLoopSound() != null) specialEffectsBuilder.ambientLoopSound(this.ambientLoopSound());
		if (this.ambientMoodSettings() != null) specialEffectsBuilder.ambientMoodSound(this.ambientMoodSettings());
		if (this.ambientAdditionsSound() != null) specialEffectsBuilder.ambientAdditionsSound(this.ambientAdditionsSound());
		if (this.backgroundMusic() != null) specialEffectsBuilder.backgroundMusic(this.backgroundMusic());

		biomeBuilder.specialEffects(specialEffectsBuilder.build());

		return biomeBuilder.build();
	}

	public abstract void addFeatures(BiomeGenerationSettings.Builder features);

	public abstract void addSpawns(MobSpawnSettings.Builder spawns);

	public ResourceKey<Biome> getKey() {
		return this.key;
	}

}
