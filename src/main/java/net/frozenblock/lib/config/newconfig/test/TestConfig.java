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
import net.frozenblock.lib.config.newconfig.entry.type.EntryType;
import net.minecraft.resources.Identifier;

public class TestConfig {
	public static final ConfigEntry<Boolean> TEST = new ConfigEntry<>(FrozenLibConstants.id("test"), EntryType.BOOL, true);
	public static final ConfigEntry<Identifier> TEST_ID = new ConfigEntry<>(FrozenLibConstants.id("test_id"), EntryType.IDENTIFIER, FrozenLibConstants.id("test_id"));
}
