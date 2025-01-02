/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.biome.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FrozenBiome {
	private static final List<FrozenBiome> BIOMES = new ArrayList<>();
	private final ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(this.modID(), this.biomeID()));

	private boolean enabled = true;

	protected FrozenBiome() {
		BIOMES.add(this);
	}

	/**
	 * @return the namespace to use for this biome.
	 */
    public abstract String modID();

	/**
	 * @return this biome's name, not including the namespace.
	 */
	public abstract String biomeID();

	/**
	 * @return thetemperature of this biome.
	 */
	public abstract float temperature();

	/**
	 * @return the chance of downfall in this biome.
	 */
	public abstract float downfall();

	/**
	 * @return whether this biome has precipitation.
	 */
	public abstract boolean hasPrecipitation();

	/**
	 * @return the temperature modifier to use for this biome.
	 */
	public Biome.TemperatureModifier temperatureModifier() {
		return Biome.TemperatureModifier.NONE;
	}

	/**
	 * @return the sky color to use for this biome.
	 */
	public abstract int skyColor();

	/**
	 * @return the fog color to use for this biome.
	 */
	public abstract int fogColor();

	/**
	 * @return the water color to use for this biome.
	 */
	public abstract int waterColor();

	/**
	 * @return the water fog color to use for this biome.
	 */
	public abstract int waterFogColor();

	/**
	 * @return the foliage color override to use for this biome.
	 */
	@Nullable
	public abstract Integer foliageColorOverride();

	/**
	 * @return the grass color override to use for the biome.
	 */
	@Nullable
	public abstract Integer grassColorOverride();

	/**
	 * @return the {@link net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier} of the biome.
	 */
	public BiomeSpecialEffects.GrassColorModifier grassColorModifier() {
		return BiomeSpecialEffects.GrassColorModifier.NONE;
	}

	/**
	 * @return the {@link AmbientParticleSettings} of the biome.
	 */
	@Nullable
	public abstract AmbientParticleSettings ambientParticleSettings();

	/**
	 * @return the {@link Holder<SoundEvent>} of the biome used for looping ambience.
	 */
	@Nullable
	public abstract Holder<SoundEvent> ambientLoopSound();

	/**
	 * @return the {@link AmbientMoodSettings} of the biome.
	 */
	@Nullable
	public abstract AmbientMoodSettings ambientMoodSettings();

	/**
	 * @return the {@link AmbientAdditionsSettings} of the biome.
	 */
	@Nullable
	public abstract AmbientAdditionsSettings ambientAdditionsSound();

	/**
	 * @return the {@link Music} of the biome.
	 */
	@Nullable
	public abstract Music backgroundMusic();

	/**
	 * Builds this biome.
	 *
	 * @param entries The boostrap context used for generating biomes.
	 * @return the finalized {@link Biome}.
	 */
	public final @NotNull Biome create(@NotNull BootstrapContext<Biome> entries) {
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

	/**
	 * Adds placed features to this biome.
	 *
	 * @param features The {@link BiomeGenerationSettings.Builder} used to create the biome's feature placement.
	 */
	public abstract void addFeatures(BiomeGenerationSettings.Builder features);

	/**
	 * Adds mob spawns to this biome.
	 *
	 * @param spawns The {@link MobSpawnSettings.Builder} used to create the biome's mob spawning lists.
	 */
	public abstract void addSpawns(MobSpawnSettings.Builder spawns);

	/**
	 * Injects this biome to overworld generation.
	 * This will be called automatically by FrozenLib unless the `disable` method is called.
	 *
	 * @param consumer The consumers used by {@link net.minecraft.world.level.biome.OverworldBiomeBuilder} to add biomes to worldgen.
	 */
	public abstract void injectToOverworld(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer);

	/**
	 * Disables this biome from being automatically injected into worldgen.
	 *
	 * <p>This may be useful in cases where you want to another mod to disable your mod's biomes from generating for the sake of compatibility.
	 */
	public void disable() {
		this.enabled = false;
	}

	/**
	 * @return whether the biome can be automatically injected into worldgen.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return the {@link ResourceKey} of the biome.
	 */
	public ResourceKey<Biome> getKey() {
		return this.key;
	}

	/**
	 * Adds this biome to world generation at the surface, like most other biomes.
	 *
	 * <p>This matches the depth of most other Vanilla surface biomes.
	 */
	public final void addSurfaceBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset) {
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(0.0F), weirdness, offset), this.getKey()));
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.0F), weirdness, offset), this.getKey()));
	}

	/**
	 * Adds this biome to world generation between the depths of a regular and deep cave biome.
	 *
	 * <p>This is between Vanilla's Lush Caves/Dripstone Caves and Deep Dark depth.
	 */
	public final void addSemiDeepBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> parameters, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset) {
		parameters.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.4F, 1.0F), weirdness, offset), this.getKey()));
	}

	/**
	 * Adds this biome to world generation at the depth of a regular cave biome.
	 *
	 * <p>This matches Vanilla's Lush Caves/Dripstone Caves depth.
	 */
	public final void addUndergroundBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset) {
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.2F, 0.9F), weirdness, offset), this.getKey()));
	}

	/**
	 * Adds this biome to world generation at the depth of a deep cave biome.
	 *
	 * <p>This matches Vanilla's Deep Dark depth.
	 */
	public final void addBottomBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter temerature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset) {
		consumer.accept(Pair.of(Climate.parameters(temerature, humidity, continentalness, erosion, Climate.Parameter.point(1.1F), weirdness, offset), this.getKey()));
	}

	/**
	 * @return the list of all FrozenBiomes.
	 */
	@NotNull
	public static ImmutableList<FrozenBiome> getFrozenBiomes() {
		return ImmutableList.copyOf(BIOMES);
	}

}
