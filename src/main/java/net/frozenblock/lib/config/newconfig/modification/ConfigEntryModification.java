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

package net.frozenblock.lib.config.newconfig.modification;

import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.api.instance.util.DeepCopyUtils;

import java.util.function.Consumer;

public record ConfigEntryModification<T>(Consumer<EntryValueHolder<T>> modifier) {

    public static <T> T modifyEntry(ConfigEntry<T> entry, T original) {
        final T copy;
        try {
            // Use DeepCopyUtils to create a shallow instance and later copy into it
            copy = DeepCopyUtils.deepCopy(original);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy config entry value", e);
        }

        copyInto(original, copy);
        final EntryValueHolder<T> holder = new EntryValueHolder<>(original);

        for (ConfigEntryModification<T> modifications : entry.modifications()) {
            modifications.modifier().accept(holder);
        }
        return holder.value;
    }

	public static <T> void copyInto(T source, T destination) {
		DeepCopyUtils.deepCopyInto(source, destination);
	}
}
