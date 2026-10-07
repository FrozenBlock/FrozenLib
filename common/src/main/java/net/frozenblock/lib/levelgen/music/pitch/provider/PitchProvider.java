/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.phys.Vec3;

public interface PitchProvider extends Validatable {
	Codec<PitchProvider> DIRECT_CODEC = FrozenLibRegistries.PITCH_PROVIDER_TYPE.byNameCodec().dispatch(PitchProvider::codec, Function.identity());
	Codec<Holder<PitchProvider>> HOLDER_CODEC = RegistryCodecs.holder(FrozenLibRegistries.MUSIC_PITCH_PROVIDER, DIRECT_CODEC);
	Codec<HolderSet<PitchProvider>> HOLDER_SET_CODEC = RegistryCodecs.holderSet(FrozenLibRegistries.MUSIC_PITCH_PROVIDER, DIRECT_CODEC);

	float sample(Context context);

	default boolean applicable(Context context) {
		return true;
	}

	MapCodec<? extends PitchProvider> codec();

	static Holder<PitchProvider> abs(Holder<PitchProvider> input) {
		return Holder.direct(new Absolute(input));
	}

	@SafeVarargs
	static Holder<PitchProvider> avg(Holder<PitchProvider>... inputs) {
		return Holder.direct(new Average(HolderSet.direct(inputs)));
	}

	static Holder<PitchProvider> ceiling(Holder<PitchProvider> input) {
		return Holder.direct(new Ceiling(input));
	}

	static Holder<PitchProvider> exactly(float value) {
		return Holder.direct(new ConstantValue(value));
	}

	static Holder<PitchProvider> cos(Holder<PitchProvider> waveLength) {
		return Holder.direct(new Cosine(waveLength));
	}

	static Holder<PitchProvider> cos(Holder<PitchProvider> waveLength, Holder<PitchProvider> amplitude) {
		return mul(cos(waveLength), amplitude);
	}

	static Holder<PitchProvider> cos(Holder<PitchProvider> waveLength, Holder<PitchProvider> amplitude, Holder<PitchProvider> midline) {
		return add(midline, cos(waveLength, amplitude));
	}

	static Holder<PitchProvider> sub(Holder<PitchProvider> provider, Holder<PitchProvider> operand) {
		return Holder.direct(new Difference(provider, operand));
	}

	static Holder<PitchProvider> forEnvironmentAttribute(EnvironmentAttribute<?> attribute) {
		return Holder.direct(new EnvironmentAttributeValue(attribute));
	}

	@SafeVarargs
	static Holder<PitchProvider> length(Holder<PitchProvider>... inputs) {
		return Holder.direct(new Length(HolderSet.direct(inputs)));
	}

	@SafeVarargs
	static Holder<PitchProvider> max(Holder<PitchProvider>... inputs) {
		return Holder.direct(new Maximum(HolderSet.direct(inputs)));
	}

	@SafeVarargs
	static Holder<PitchProvider> min(Holder<PitchProvider>... inputs) {
		return Holder.direct(new Minimum(HolderSet.direct(inputs)));
	}

	static Holder<PitchProvider> mod(Holder<PitchProvider> left, Holder<PitchProvider> right) {
		return Holder.direct(new Modulus(left, right));
	}

	static Holder<PitchProvider> negate(Holder<PitchProvider> input) {
		return Holder.direct(new Negate(input));
	}

	static Holder<PitchProvider> pow(Holder<PitchProvider> base, Holder<PitchProvider> exponent) {
		return Holder.direct(new Power(base, exponent));
	}

	@SafeVarargs
	static Holder<PitchProvider> mul(Holder<PitchProvider>... inputs) {
		return Holder.direct(new Product(HolderSet.direct(inputs)));
	}

	static Holder<PitchProvider> div(Holder<PitchProvider> left, Holder<PitchProvider> right) {
		return Holder.direct(new Quotient(left, right));
	}

	static Holder<PitchProvider> round(Holder<PitchProvider> input) {
		return Holder.direct(new Round(input));
	}

	static Holder<PitchProvider> sin(Holder<PitchProvider> input) {
		return Holder.direct(new Sine(input));
	}

	static Holder<PitchProvider> sqrt(Holder<PitchProvider> input) {
		return Holder.direct(new SquareRoot(input));
	}

	@SafeVarargs
	static Holder<PitchProvider> add(Holder<PitchProvider>... inputs) {
		return Holder.direct(new Sum(HolderSet.direct(inputs)));
	}

	static Holder<PitchProvider> trunc(Holder<PitchProvider> input) {
		return Holder.direct(new Truncate(input));
	}

	static Holder<PitchProvider> clamp(Holder<PitchProvider> input, Holder<PitchProvider> min, Holder<PitchProvider> max) {
		return max(min(input, min), max);
	}

	static Holder<PitchProvider> configPredicate(Holder<ConfigPredicate> predicate, Holder<PitchProvider> provider) {
		return Holder.direct(new ConfigRequirement(predicate, provider));
	}

	static Holder<PitchProvider> conditional(Holder<ConfigPredicate> selector, Holder<PitchProvider> onTrue, Holder<PitchProvider> onFalse) {
		return Holder.direct(new ConditionalValue(selector, onTrue, onFalse));
	}

	static Holder<PitchProvider> biomes(HolderSet<Biome> biomes, Holder<PitchProvider> provider) {
		return Holder.direct(new BiomeRequirement(biomes, provider));
	}

	static Holder<PitchProvider> biome(Holder<Biome> biome, Holder<PitchProvider> provider) {
		return biomes(HolderSet.direct(biome), provider);
	}

	static Holder<PitchProvider> structuresFromIds(List<Pair<Identifier, Boolean>> requirements, Holder<PitchProvider> provider) {
		return Holder.direct(new StructureRequirement(requirements, provider));
	}

	static Holder<PitchProvider> structuresFromKeys(List<Pair<ResourceKey<Structure>, Boolean>> requirements, Holder<PitchProvider> provider) {
		return structuresFromIds(
			requirements.stream().map(pair -> Pair.of(pair.getFirst().identifier(), pair.getSecond())).toList(),
			provider
		);
	}

	static Holder<PitchProvider> structure(Identifier structure, boolean requireInsidePiece, Holder<PitchProvider> provider) {
		return structuresFromIds(List.of(Pair.of(structure, requireInsidePiece)), provider);
	}

	static Holder<PitchProvider> structure(ResourceKey<Structure> structure, boolean requireInsidePiece, Holder<PitchProvider> provider) {
		return structure(structure.identifier(), requireInsidePiece, provider);
	}

	static Holder<PitchProvider> dimensions(List<Identifier> dimensions, Holder<PitchProvider> provider) {
		return Holder.direct(new DimensionRequirement(dimensions, provider));
	}

	static Holder<PitchProvider> dimension(Identifier dimension, Holder<PitchProvider> provider) {
		return dimensions(List.of(dimension), provider);
	}

	record Context(Level level, Identifier dimension, Holder<Biome> biome, Optional<StructureStatus> structureStatus, Vec3 origin, long gameTime) {}
}
