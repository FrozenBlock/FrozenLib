/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.core.registry.api.sync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.frozenblock.lib.platform.ModLoader;
import org.slf4j.Logger;

public class ModProtocol {
	public static final Event<LoadModProtocol> LOAD_MOD_PROTOCOL = FrozenEvents.createEnvironmentEvent(LoadModProtocol.class, callbacks -> () -> {
		for (var callback : callbacks) callback.load();
	});
	public static final List<ModProtocolDef> REQUIRED = new ArrayList<>();
	public static final List<ModProtocolDef> ALL = new ArrayList<>();
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<String, ModProtocolDef> PROTOCOL_VERSIONS = new HashMap<>();
	public static boolean enabled = false;
	public static boolean disableQuery = false;
	public static String prioritizedId = "";
	public static ModProtocolDef prioritizedEntry;

	@SuppressWarnings("ConstantConditions")
	public static void loadVersions() {
		LOAD_MOD_PROTOCOL.invoker().load();

		// TODO: see if ModProtocol is possible on NeoForge
		for (ModLoader.ModEntry mod : ModLoader.getAllMods()) {
			final var frozenRegistryOpt = mod.getCustomData("frozenlib_registry");
			if (frozenRegistryOpt.isEmpty()) continue;

			final JsonElement frozenRegistry = frozenRegistryOpt.get();

			if (!frozenRegistry.isJsonObject()) {
				LOGGER.warn("Mod {} ({}) contains invalid 'frozenlib_registry' entry! Expected 'OBJECT', found '{}'",
					mod.getName(), mod.getId(), frozenRegistry.getClass().getSimpleName());
				continue;
			}

			final var value = frozenRegistry.getAsJsonObject().get("mod_protocol");
			if (value == null || value.isJsonNull()) continue;

			if (value.isJsonObject()) {
				final var object = value.getAsJsonObject();

				var optional = false;
				final var optVal = object.get("optional");

				if (optVal != null) {
					if (!optVal.isJsonPrimitive() || !optVal.getAsJsonPrimitive().isBoolean()) {
						invalidEntryType(".optional", mod, "BOOLEAN", optVal.getClass().getSimpleName());
						continue;
					}
					optional = optVal.getAsBoolean();
				}

				var version = decodeVersion(".value", mod, object.get("value"));
				if (version != null) add(new ModProtocolDef("mod:" + mod.getId(), mod.getName(), version, optional));
			} else {
				final var version = decodeVersion("", mod, value);
				if (version != null) add(new ModProtocolDef("mod:" + mod.getId(), mod.getName(), version, false));
			}
		}
	}

	private static IntList decodeVersion(String path, ModLoader.ModEntry mod, JsonElement value) {
		if (value == null || value.isJsonNull()) {
			invalidEntryType(path, mod, "NUMBER", "NULL");
			return null;
		}

		if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
			final int version = value.getAsInt();
			if (version < 0) {
				negativeEntry(path, mod, version);
				return null;
			}
			return IntList.of(version);
		}

		if (value.isJsonArray()) {
			final JsonArray array = value.getAsJsonArray();
			final var versions = new IntArrayList(array.size());
			for (int i = 0; i < array.size(); i++) {
				final var entry = array.get(i);
				if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isNumber()) {
					final var version = entry.getAsInt();
					if (version < 0) {
						negativeEntry(path + "[" + i + "]", mod, version);
						return null;
					}
					versions.add(version);
				} else {
					invalidEntryType(path + "[" + i + "]", mod, "NUMBER", entry.getClass().getSimpleName());
					return null;
				}
			}
			return versions;
		}

		invalidEntryType(path + ".optional", mod, "NUMBER", value.getClass().getSimpleName());
		return null;
	}

	private static void invalidEntryType(String path, ModLoader.ModEntry mod, String expected, String found) {
		LOGGER.warn("Mod {} ({}) contains invalid 'frozenlib_registry.mod_protocol{}' entry! Expected '{}', found '{}'",
			path, mod.getName(), mod.getId(), expected, found);
	}

	private static void negativeEntry(String path, ModLoader.ModEntry mod, int i) {
		LOGGER.warn("Mod {} ({}) contains invalid 'frozenlib_registry.mod_protocol{}' entry! Protocol requires non-negative integer, found '{}'!",
			path, mod.getName(), mod.getId(), i);
	}

	public static IntList getVersion(String string) {
		final var x = PROTOCOL_VERSIONS.get(string);
		return x == null ? IntList.of() : x.versions();
	}

	public static void add(ModProtocolDef def) {
		PROTOCOL_VERSIONS.put(def.id(), def);
		if (!def.optional()) REQUIRED.add(def);

		ALL.add(def);
		enabled = true;
	}

	@FunctionalInterface
	public interface LoadModProtocol extends CommonEventEntrypoint {
		void load();
	}
}
