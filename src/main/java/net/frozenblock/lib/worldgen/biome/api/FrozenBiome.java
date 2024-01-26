/*
 * Copyright 2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
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
	private final ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, new ResourceLocation(this.modID(), this.biomeID()));

	protected FrozenBiome() {
		BIOMES.add(this);
	}

    public abstract String modID();

	public abstract String biomeID();

	public abstract float temperature();

	public abstract float downfall();

	public abstract boolean hasPrecipitation();

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

	public abstract void addFeatures(BiomeGenerationSettings.Builder features);

	public abstract void addSpawns(MobSpawnSettings.Builder spawns);

	public abstract void injectToOverworld(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer);

	public ResourceKey<Biome> getKey() {
		return this.key;
	}

	protected final void addSurfaceBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness) {
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(0.0F), depth, weirdness), this.getKey()));
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.0F), depth, weirdness), this.getKey()));
	}

	protected final void addSemiDeepBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> parameters, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset) {
		parameters.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.4F, 1.0F), weirdness, offset), this.getKey()));
	}

	protected final void addUndergroundBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness) {
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.2F, 0.9F), depth, weirdness), this.getKey()));
	}

	protected final void addBottomBiome(@NotNull Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, Climate.Parameter temerature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness) {
		consumer.accept(Pair.of(Climate.parameters(temerature, humidity, continentalness, erosion, Climate.Parameter.point(1.1F), depth, weirdness), this.getKey()));
	}

	@NotNull
	public static ImmutableList<FrozenBiome> getFrozenBiomes() {
		return ImmutableList.copyOf(BIOMES);
	}

}
