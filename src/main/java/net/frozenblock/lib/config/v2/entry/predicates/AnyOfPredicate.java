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
import java.util.List;

public class AnyOfPredicate extends CombiningPredicate {
	public static final MapCodec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

	public AnyOfPredicate(List<ConfigPredicate> predicates) {
		super(predicates);
	}

	@Override
	public Boolean get() {
		for (ConfigPredicate predicate : this.predicates) {
			if (predicate.get()) return true;
		}
		return false;
	}

	@Override
	public MapCodec<AnyOfPredicate> codec() {
		return CODEC;
	}
}
