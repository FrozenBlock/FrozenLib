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

public record ConfigPredicateSelectorPitch(Holder<ConfigPredicate> predicate, PitchProvider whenTrue, PitchProvider whenFalse) implements PitchProvider {
	public static MapCodec<ConfigPredicateSelectorPitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.HOLDER_CODEC.fieldOf("selector").forGetter(ConfigPredicateSelectorPitch::predicate),
		DIRECT_CODEC.fieldOf("when_true").forGetter(ConfigPredicateSelectorPitch::whenTrue),
		DIRECT_CODEC.fieldOf("when_false").forGetter(ConfigPredicateSelectorPitch::whenFalse)
	).apply(instance, ConfigPredicateSelectorPitch::new));

	@Override
	public float sample(long gameTime) {
		return (this.predicate.value().test() ? this.whenTrue : this.whenFalse).sample(gameTime);
	}

	@Override
	public MapCodec<ConfigPredicateSelectorPitch> codec() {
		return CODEC;
	}
}
