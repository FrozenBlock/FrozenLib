/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.config.newconfig.entry.condition;

import java.util.function.Predicate;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;

public class EntryCondition<T> extends AbstractCondition {
	private final ConfigEntry<T> entry;
	private final Predicate<ConfigEntry<T>> predicate;

	public EntryCondition(ConfigEntry<T> entry, Predicate<ConfigEntry<T>> predicate) {
		this.entry = entry;
		this.predicate = predicate;
	}

	@Override
	public boolean test() {
		return this.predicate.test(this.entry);
	}
}
