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
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;

public class WithFallbackPredicate implements ConfigPredicate {
	public static final MapCodec<WithFallbackPredicate> CODEC = ID.CODEC.fieldOf("entry").dispatchMap(
		predicate -> predicate.id,
		id -> RecordCodecBuilder.mapCodec(instance -> {
			final ConfigEntry<?> entry = ConfigV2Registry.getEntry(id);
			if (entry == null) {
				return instance.group(
					instance.point(id),
					ConfigPredicate.CODEC.fieldOf("fallback").forGetter(predicate -> predicate.fallback)
				).apply(instance, WithFallbackPredicate::whenNotPresent);
			}

			return instance.group(
				instance.point(id),
				ConfigPredicate.CODEC.fieldOf("predicate").forGetter(predicate -> predicate.whenPresent.orElseThrow()),
				ConfigPredicate.CODEC.fieldOf("fallback").forGetter(predicate -> predicate.fallback)
			).apply(instance, WithFallbackPredicate::of);
		})
	);
	private final ID id;
	private final Optional<ConfigPredicate> whenPresent;
	private final ConfigPredicate fallback;

	private WithFallbackPredicate(ID id, Optional<ConfigPredicate> whenPresent, ConfigPredicate fallback) {
		this.id = id;
		this.whenPresent = whenPresent;
		this.fallback = fallback;
	}

	protected static WithFallbackPredicate whenNotPresent(ID id, ConfigPredicate fallback) {
		return new WithFallbackPredicate(id, Optional.empty(), fallback);
	}

	public static WithFallbackPredicate of(ID id, ConfigPredicate predicate, ConfigPredicate fallback) {
		return new WithFallbackPredicate(id, Optional.of(predicate), fallback);
	}

	@Override
	public Boolean get() {
		return this.whenPresent.map(ConfigPredicate::get).orElseGet(this.fallback);
	}

	@Override
	public MapCodec<WithFallbackPredicate> codec() {
		return CODEC;
	}
}
