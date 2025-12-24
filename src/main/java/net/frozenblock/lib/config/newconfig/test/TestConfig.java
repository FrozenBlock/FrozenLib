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

package net.frozenblock.lib.config.newconfig.test;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.entry.BooleanConfigEntry;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;

public class TestConfig {
	public static final BooleanConfigEntry TEST = BooleanConfigEntry.createSimple(FrozenLibConstants.id("test"), true);

	public static ConfigEntry<?> register(ConfigEntry<?> entry) {
		return Registry.register(FrozenLibRegistries.CONFIG_ENTRY, entry.getId(), entry);
	}
}
