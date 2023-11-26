/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.api.instance;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Consumer;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.config.api.annotation.UnsyncableEntry;
import net.frozenblock.lib.config.api.network.ConfigSyncModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for modifying configs
 * @param modification The consumer for applying modifications
 * @param <T> The type of the config class
 */
public record ConfigModification<T>(Consumer<T> modification) {
    public static <T> T modifyConfig(Config<T> config, T original, boolean excludeSync) {
        try {
			// clone
			T instance = config.configClass().getConstructor().newInstance();
			copyInto(original, instance);

			// modify
			var list = ConfigRegistry.getModificationsForConfig(config)
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.toList();

			for (Map.Entry<ConfigModification<T>, Integer> modification : list) {
				var consumer = modification.getKey().modification;
				if (excludeSync && consumer instanceof ConfigSyncModification) continue;
				modification.getKey().modification.accept(instance);
			}
			return instance;
		} catch (Exception e) {
			FrozenLogUtils.error("Failed to modify config, returning original.", true, e);
			return original;
		}
    }

    public static <T> void copyInto(@NotNull T source, T destination, boolean isSyncModification) {
        Class<?> clazz = source.getClass();
        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
				if (isSyncModification && field.isAnnotationPresent(UnsyncableEntry.class)) continue;
                try {
                    field.set(destination, field.get(source));
                } catch (IllegalAccessException e) {
					FrozenLogUtils.error("Failed to copy field " + field.getName(), true, e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

	public static <T> void copyInto(@NotNull T source, T destination) {
		copyInto(source, destination, false);
	}
}
