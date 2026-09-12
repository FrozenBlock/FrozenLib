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

package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.HolderSet;

public abstract class CombiningPredicate implements ConfigPredicate {
	protected final HolderSet<ConfigPredicate> predicates;

	protected CombiningPredicate(HolderSet<ConfigPredicate> predicates) {
		this.predicates = predicates;
	}

	public static <T extends CombiningPredicate> MapCodec<T> codec(Function<HolderSet<ConfigPredicate>, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			HOLDER_SET_CODEC.fieldOf("predicates").forGetter(predicate -> predicate.predicates)
		).apply(instance, constructor));
	}
}
