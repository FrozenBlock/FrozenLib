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

package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.frozenblock.lib.registry.FrozenLibBuiltInRegistries;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

// TODO: implement validation *after* deserialization
public abstract class BiomeModification implements Validatable {
	public static final Codec<BiomeModification> DIRECT_CODEC = FrozenLibBuiltInRegistries.BIOME_MODIFICATION_TYPE.byNameCodec()
		.dispatch(BiomeModification::type, BiomeModificationType::codec);
	public static final Codec<BiomeModification> NETWORK_CODEC = FrozenLibBuiltInRegistries.BIOME_MODIFICATION_TYPE.byNameCodec()
		.dispatch(
			BiomeModification::type,
			type -> type.syncToClient() ? type.codec() : NoOpModification.createDummyCodec()
		);
	static final String SELECTOR_KEY = "selector";
	static final String MODIFICATION_PHASE_KEY = "phase";
	static final MapCodec<Holder<BiomeSelector>> SELECTOR_MAP_CODEC = BiomeSelector.HOLDER_CODEC.fieldOf(SELECTOR_KEY);
	static final MapCodec<ModificationPhase> MODIFICATION_PHASE_MAP_CODEC = ModificationPhase.CODEC.fieldOf(MODIFICATION_PHASE_KEY);
	private final Holder<BiomeSelector> selector;
	private final ModificationPhase phase;

	protected BiomeModification(Holder<BiomeSelector> selector, ModificationPhase phase) {
		this.selector = selector;
		this.phase = phase;
	}

	public abstract void modify(BiomeModificationContext context);

	public boolean selectorMatches(BiomeSelectionContext context) {
		return this.selector.value().test(context);
	}

	protected Holder<BiomeSelector> selector() {
		return this.selector;
	}

	protected ModificationPhase phase() {
		return this.phase;
	}

	abstract BiomeModificationType<? extends BiomeModification> type();

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, SELECTOR_KEY, this.selector);
	}

	public static ResourceKey<BiomeModification> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.BIOME_MODIFICATION, id);
	}

	public static ResourceKey<BiomeModification> createKey(TagKey<Biome> biomeTag) {
		return createKey(biomeTag.location());
	}

	public static final class EmptyModificationProblem implements ProblemReporter.Warning {
		private final boolean anyOf;
		private final List<String> fieldNames;

		EmptyModificationProblem(boolean anyOf, String... fieldNames) {
			this.anyOf = anyOf;
			this.fieldNames = List.of(fieldNames);
		}

		EmptyModificationProblem(String fieldName) {
			this(false, fieldName);
		}

		@Override
		public String description() {
			if (this.fieldNames.size() > 1) {
				final StringBuilder builder = new StringBuilder();
				builder.append("Expected fields ");

				final String gate = this.anyOf ? "or" : "and";
				final int fieldNameCount = this.fieldNames.size();
				for (int i = 0; i < fieldNameCount; ++i) {
					final String fieldName = this.fieldNames.get(i);
					builder.append(
						i == fieldNameCount - 1
							? gate + " '" + fieldName + "' "
							: "'" + this.fieldNames.get(i) + "'" + (fieldNameCount == 2 ? " " : ", ")
					);
				}

				builder.append("to contain values, but are empty instead");
				return builder.toString();
			}

			return "Expected field '" + this.fieldNames.getFirst() + "' to contain values, but is empty instead";
		}
	}

	public record EmptyGenerationStepInListProblem(String fieldName) implements ProblemReporter.Error {
		@Override
		public String description() {
			return "Elements of " + this.fieldName + " must always contain a generation step";
		}
	}
}
