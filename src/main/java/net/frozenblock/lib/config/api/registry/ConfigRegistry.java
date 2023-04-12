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

package net.frozenblock.lib.config.api.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.instance.Config;

public class ConfigRegistry {

	private static final List<Config<?>> CONFIG_REGISTRY = new ArrayList<>();

	private static final Map<String, List<TypedEntryType<?>>> MOD_TYPED_ENTRY_REGISTRY = new HashMap<>();
	private static final List<TypedEntryType<?>> TYPED_ENTRY_REGISTRY = new ArrayList<>();

	public static <T> Config<T> register(Config<T> config) {
		if (CONFIG_REGISTRY.contains(config)) {
			throw new IllegalStateException("Config already registered.");
		}
		CONFIG_REGISTRY.add(config);
		return config;
	}

	public static boolean contains(Config<?> config) {
		return CONFIG_REGISTRY.contains(config);
	}

	public static <T> TypedEntryType<T> register(TypedEntryType<T> entry) {
		if (TYPED_ENTRY_REGISTRY.contains(entry)) {
			throw new IllegalStateException("Typed entry already registered.");
		}
		MOD_TYPED_ENTRY_REGISTRY.computeIfAbsent(entry.modId(), key -> new ArrayList<>()).add(entry);
		TYPED_ENTRY_REGISTRY.add(entry);
		return entry;
	}

	public static boolean contains(TypedEntryType<?> entry) {
		return TYPED_ENTRY_REGISTRY.contains(entry);
	}

	public static Collection<TypedEntryType<?>> getForMod(String modId) {
		return Map.copyOf(MOD_TYPED_ENTRY_REGISTRY).getOrDefault(modId, new ArrayList<>());
	}

	public static Collection<TypedEntryType<?>> getAll() {
		return TYPED_ENTRY_REGISTRY;
	}
}
