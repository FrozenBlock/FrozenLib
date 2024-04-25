/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.item.api;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class ItemBlockStateTagUtils {

	public static <T extends Comparable<T>> T getProperty(@NotNull ItemStack stack, Property<T> property, T defaultValue) {
		BlockItemStateProperties blockItemStateProperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		if (!blockItemStateProperties.isEmpty()) {
			var properties = blockItemStateProperties.properties();
			String stringValue = property.getName();
			if (properties.containsKey(stringValue)) {
				return property.getValue(properties.get(stringValue)).orElse(defaultValue);
			}
		}
		return defaultValue;
	}

	public static boolean getBoolProperty(@NotNull ItemStack stack, BooleanProperty property, boolean orElse) {
		BlockItemStateProperties blockItemStateProperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		if (!blockItemStateProperties.isEmpty()) {
			var properties = blockItemStateProperties.properties();
			String stringValue = property.getName();
			if (properties.containsKey(stringValue)) {
				return properties.get(stringValue).equals("true");
			}
		}
		return orElse;
	}

	public static <T extends Comparable<T>> void setProperty(@NotNull ItemStack stack, @NotNull Property<T> property, T value) {
		BlockItemStateProperties blockItemStateProperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		stack.set(DataComponents.BLOCK_STATE, blockItemStateProperties.with(property, value));
	}

	@NotNull
	private static CompoundTag getOrCreateBlockStateTag(@NotNull CompoundTag compoundTag) {
		CompoundTag blockStateTag;
		if (compoundTag.contains("BlockStateTag", Tag.TAG_COMPOUND)) {
			blockStateTag = compoundTag.getCompound("BlockStateTag");
		} else {
			blockStateTag = new CompoundTag();
			compoundTag.put("BlockStateTag", blockStateTag);
		}
		return blockStateTag;
	}
}
