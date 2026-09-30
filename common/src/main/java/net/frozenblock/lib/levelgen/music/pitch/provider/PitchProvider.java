package net.frozenblock.lib.levelgen.music.pitch.provider;

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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface PitchProvider {
	Codec<PitchProvider> DIRECT_CODEC = FrozenLibRegistries.PITCH_PROVIDER_TYPE.byNameCodec().dispatch(PitchProvider::codec, Function.identity());

	float sample(long gameTime);

	default boolean applicable(Context context) {
		return true;
	}

	MapCodec<? extends PitchProvider> codec();

	static PitchProvider constant(float value) {
		return new Constant(value);
	}

	static PitchProvider add(PitchProvider provider, PitchProvider operand) {
		return new Add(provider, operand);
	}

	static PitchProvider add(PitchProvider provider, float operand) {
		return add(provider, constant(operand));
	}

	static PitchProvider subtract(PitchProvider provider, PitchProvider operand) {
		return new Subtract(provider, operand);
	}

	static PitchProvider subtract(PitchProvider provider, float operand) {
		return subtract(provider, constant(operand));
	}

	static PitchProvider multiply(PitchProvider provider, PitchProvider operand) {
		return new Multiply(provider, operand);
	}

	static PitchProvider multiply(PitchProvider provider, float operand) {
		return multiply(provider, constant(operand));
	}

	static PitchProvider divide(PitchProvider provider, PitchProvider operand) {
		return new Divide(provider, operand);
	}

	static PitchProvider divide(PitchProvider provider, float operand) {
		return divide(provider, constant(operand));
	}

	static PitchProvider sine(float waveLength) {
		return new Sine(waveLength);
	}

	static PitchProvider sine(float waveLength, float amplitude) {
		return multiply(sine(waveLength), constant(amplitude));
	}

	static PitchProvider sine(float waveLength, float amplitude, float midline) {
		return add(constant(midline), sine(waveLength, amplitude));
	}

	static PitchProvider cosine(float waveLength) {
		return new Cosine(waveLength);
	}

	static PitchProvider cosine(float waveLength, float amplitude) {
		return multiply(cosine(waveLength), constant(amplitude));
	}

	static PitchProvider cosine(float waveLength, float amplitude, float midline) {
		return add(constant(midline), cosine(waveLength, amplitude));
	}

	static PitchProvider clamped(PitchProvider provider, PitchProvider min, PitchProvider max) {
		return new ClampedPitch(provider, min, max);
	}

	static PitchProvider clamped(PitchProvider provider, float min, float max) {
		return clamped(provider, constant(min), constant(max));
	}

	static PitchProvider whenTrue(Holder<ConfigPredicate> predicate, PitchProvider provider) {
		return new ConfigPredicatePitch(predicate, provider);
	}

	static PitchProvider whenTrue(ConfigPredicate predicate, PitchProvider provider) {
		return whenTrue(predicate.asHolder(), provider);
	}

	static PitchProvider selector(Holder<ConfigPredicate> selector, PitchProvider whenTrue, PitchProvider whenFalse) {
		return new ConfigPredicateSelectorPitch(selector, whenTrue, whenFalse);
	}

	static PitchProvider selector(ConfigPredicate selector, PitchProvider whenTrue, PitchProvider whenFalse) {
		return selector(selector.asHolder(), whenTrue, whenFalse);
	}

	static PitchProvider biomes(HolderSet<Biome> biomes, PitchProvider provider) {
		return new BiomeProvider(biomes, provider);
	}

	static PitchProvider biome(Holder<Biome> biome, PitchProvider provider) {
		return biomes(HolderSet.direct(biome), provider);
	}

	static PitchProvider structuresFromIds(List<Identifier> structures, boolean requireInsidePiece, PitchProvider provider) {
		return new StructureProvider(structures, requireInsidePiece, provider);
	}

	static PitchProvider structuresFromKeys(List<ResourceKey<Structure>> structures, boolean requireInsidePiece, PitchProvider provider) {
		return structuresFromIds(structures.stream().map(ResourceKey::identifier).toList(), requireInsidePiece, provider);
	}

	static PitchProvider structure(Identifier structure, boolean requireInsidePiece, PitchProvider provider) {
		return structuresFromIds(List.of(structure), requireInsidePiece, provider);
	}

	static PitchProvider structure(ResourceKey<Structure> structure, boolean requireInsidePiece, PitchProvider provider) {
		return structure(structure.identifier(), requireInsidePiece, provider);
	}

	static PitchProvider dimensions(List<Identifier> dimensions, PitchProvider provider) {
		return new DimensionProvider(dimensions, provider);
	}

	static PitchProvider dimension(Identifier dimension, PitchProvider provider) {
		return dimensions(List.of(dimension), provider);
	}

	record Context(Level level, Identifier dimension, Holder<Biome> biome, Optional<StructureStatus> structureStatus) {}
}
