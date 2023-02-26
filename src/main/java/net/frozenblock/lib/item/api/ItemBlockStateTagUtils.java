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
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ItemBlockStateTagUtils {

	public static int getIntProperty(ItemStack stack, String property) {
		if (stack.getTag() != null) {
			CompoundTag stateTag = stack.getTag().getCompound("BlockStateTag");
			if (stateTag != null) {
				return Integer.parseInt(stateTag.getString(property));
			}
		}
		return 0;
	}

	public static boolean getBoolProperty(ItemStack stack, String property) {
		if (stack.getTag() != null) {
			CompoundTag stateTag = stack.getTag().getCompound("BlockStateTag");
			if (stateTag != null) {
				return stateTag.getString(property).equals("true");
			}
		}
		return false;
	}

	public static void setIntProperty(ItemStack stack, IntegerProperty property, int value) {
		CompoundTag stateTag = stack.getOrCreateTag().getCompound("BlockStateTag");
		stateTag.putString(property.getName(), property.getName(value));
	}

	public static void setBoolProperty(ItemStack stack, BooleanProperty property, boolean value) {
		CompoundTag stateTag = stack.getOrCreateTag().getCompound("BlockStateTag");
		stateTag.putString(property.getName(), property.getName(value));
	}

}
