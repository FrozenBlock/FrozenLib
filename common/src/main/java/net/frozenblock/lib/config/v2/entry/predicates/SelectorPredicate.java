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

public class SelectorPredicate implements ConfigPredicate {
	public static final MapCodec<SelectorPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("selector").forGetter(predicate -> predicate.selector),
		ConfigPredicate.CODEC.fieldOf("when_true").forGetter(predicate -> predicate.whenTrue),
		ConfigPredicate.CODEC.fieldOf("when_false").forGetter(predicate -> predicate.whenFalse)
	).apply(instance, SelectorPredicate::new));
	private final ConfigPredicate selector;
	private final ConfigPredicate whenTrue;
	private final ConfigPredicate whenFalse;

	public SelectorPredicate(ConfigPredicate selector, ConfigPredicate whenTrue, ConfigPredicate whenFalse) {
		this.selector = selector;
		this.whenTrue = whenTrue;
		this.whenFalse = whenFalse;
	}

	@Override
	public Boolean get() {
		return this.selector.get()
			? this.whenTrue.get()
			: this.whenFalse.get();
	}

	@Override
	public MapCodec<SelectorPredicate> codec() {
		return CODEC;
	}
}
