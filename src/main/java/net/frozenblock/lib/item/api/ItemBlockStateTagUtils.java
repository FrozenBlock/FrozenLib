/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.item.api;

import java.util.Map;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

@UtilityClass
public class ItemBlockStateTagUtils {

	public static <T extends Comparable<T>> T getProperty(ItemStack stack, Property<T> property, T defaultValue) {
		final BlockItemStateProperties stateProperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		if (stateProperties.isEmpty()) return defaultValue;

		final Map<String, String> properties = stateProperties.properties();
		final String stringValue = property.getName();
		if (properties.containsKey(stringValue)) return property.getValue(properties.get(stringValue)).orElse(defaultValue);
		return defaultValue;
	}

	public static <T extends Comparable<T>> void setProperty(ItemStack stack, Property<T> property, T value) {
		final BlockItemStateProperties stateProperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		stack.set(DataComponents.BLOCK_STATE, stateProperties.with(property, value));
	}

}
