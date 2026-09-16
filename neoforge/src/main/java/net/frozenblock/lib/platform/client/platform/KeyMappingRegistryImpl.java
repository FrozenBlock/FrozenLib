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

package net.frozenblock.lib.platform.client.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@ClientOnly
public final class KeyMappingRegistryImpl {
	private static final List<KeyMapping> PENDING_KEY_MAPPINGS = new ArrayList<>();

	public static KeyMapping register(KeyMapping keyMapping) {
		Objects.requireNonNull(keyMapping, "KeyMapping cannot be null!");
		PENDING_KEY_MAPPINGS.add(keyMapping);
		return keyMapping;
	}

	public static void flushKeyMappings(RegisterKeyMappingsEvent event) {
		for (KeyMapping keyMapping : PENDING_KEY_MAPPINGS) event.register(keyMapping);
		PENDING_KEY_MAPPINGS.clear();
	}
}
