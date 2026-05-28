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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;

public abstract class ValuePredicate<T> implements ConfigPredicate {
	final ID id;
	final ConfigEntry<T> entry;
	final T target;

	protected ValuePredicate(ID id, T target) {
		this.id = id;
		this.entry = (ConfigEntry<T>) ConfigV2Registry.getEntry(id);
		this.target = target;
	}

	public static <T extends ValuePredicate<?>> MapCodec<T> codec(BiFunction<ID, Object, T> constructor) {
		return ID.CODEC.fieldOf("entry").dispatchMap(
			predicate -> predicate.id,
			id -> RecordCodecBuilder.mapCodec(instance -> instance.group(
				instance.point(id),
				((Codec<Object>) ConfigV2Registry.getEntry(id).codec()).fieldOf("target").forGetter(predicate -> predicate.target)
			).apply(instance, constructor))
		);
	}
}
