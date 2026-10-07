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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record ConditionalValue(Holder<ConfigPredicate> condition, Holder<PitchProvider> onTrue, Holder<PitchProvider> onFalse) implements PitchProvider {
	public static MapCodec<ConditionalValue> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.HOLDER_CODEC.fieldOf("condition").forGetter(ConditionalValue::condition),
		HOLDER_CODEC.fieldOf("on_true").forGetter(ConditionalValue::onTrue),
		HOLDER_CODEC.fieldOf("on_false").forGetter(ConditionalValue::onFalse)
	).apply(instance, ConditionalValue::new));

	@Override
	public MapCodec<ConditionalValue> codec() {
		return MAP_CODEC;
	}

	@Override
	public float sample(Context context) {
		return (this.condition.value().test() ? this.onTrue : this.onFalse).value().sample(context);
	}

	@Override
	public boolean applicable(Context context) {
		return (this.condition.value().test() ? this.onTrue : this.onFalse).value().applicable(context);
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "on_true", this.onTrue);
		Validatable.validateHolder(context, "on_false", this.onFalse);
	}
}
