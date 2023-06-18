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

package net.frozenblock.lib.item.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class ItemBlockStateTagUtils {

	public static <T extends Comparable<T>> T getProperty(@NotNull ItemStack stack, Property<T> property, T defaultValue) {
		if (stack.getTag() != null) {
			CompoundTag stateTag = stack.getTag().getCompound("BlockStateTag");
			String stringValue = property.getName();
			if (stateTag.contains(stringValue)) {
				return property.getValue(stateTag.getString(stringValue)).get();
			}
		}
		return defaultValue;
	}

	public static boolean getBoolProperty(@NotNull ItemStack stack, BooleanProperty property, boolean orElse) {
		if (stack.getTag() != null) {
			CompoundTag stateTag = stack.getTag().getCompound("BlockStateTag");
			String stringValue = property.getName();
			if (stateTag.contains(stringValue)) {
				return stateTag.getString(stringValue).equals("true");
			}
		}
		return orElse;
	}

	public static <T extends Comparable<T>> void setProperty(@NotNull ItemStack stack, @NotNull Property<T> property, T value) {
		CompoundTag stateTag = getOrCreateBlockStateTag(stack.getOrCreateTag());
		stateTag.putString(property.getName(), property.getName(value));
	}

	@NotNull
	private static CompoundTag getOrCreateBlockStateTag(@NotNull CompoundTag compoundTag) {
		CompoundTag blockStateTag;
		if (compoundTag.contains("BlockStateTag", 10)) {
			blockStateTag = compoundTag.getCompound("BlockStateTag");
		} else {
			blockStateTag = new CompoundTag();
			compoundTag.put("BlockStateTag", blockStateTag);
		}
		return blockStateTag;
	}
}
