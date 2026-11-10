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

package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record ConditionalSelector(Holder<BiomeSelector> condition, Holder<BiomeSelector> onTrue, Holder<BiomeSelector> onFalse) implements BiomeSelector {
	public static MapCodec<ConditionalSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		HOLDER_CODEC.fieldOf("condition").forGetter(ConditionalSelector::condition),
		HOLDER_CODEC.fieldOf("on_true").forGetter(ConditionalSelector::onTrue),
		HOLDER_CODEC.fieldOf("on_false").forGetter(ConditionalSelector::onFalse)
	).apply(instance, ConditionalSelector::new));

	@Override
	public boolean test(BiomeSelectionContext context) {
		return this.condition.value().test(context) ? this.onTrue.value().test(context) : this.onFalse.value().test(context);
	}

	@Override
	public MapCodec<ConditionalSelector> codec() {
		return CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "condition", this.condition);
		Validatable.validateHolder(context, "on_true", this.onTrue);
		Validatable.validateHolder(context, "on_false", this.onFalse);
	}
}
