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

package net.frozenblock.lib.config.newconfig.tooltip;

import net.minecraft.network.chat.Component;

public abstract class ConfigEntryValueNameProvider<T> {
	public static final ConfigEntryValueNameProvider<Boolean> BOOL = new ConfigEntryValueNameProvider<>() {
		@Override
		public Component getValueName(Boolean value) {
			return value ? Component.literal("TRUE") : Component.literal("FALSE");
		}
	};

	public abstract Component getValueName(T value);

}
