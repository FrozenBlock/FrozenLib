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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HeavyItemDamageRegistry {

	private static final Object2ObjectOpenHashMap<Item, HeavyItemDamage> HEAVY_ITEM_DAMAGE = new Object2ObjectOpenHashMap<>();

	public static void register(Item item, float startDamage, float maxStackDamage) {
		HEAVY_ITEM_DAMAGE.put(item, new HeavyItemDamage(startDamage, maxStackDamage));
	}

	public static float getDamage(@NotNull ItemStack stack) {
		Item item = stack.getItem();
		if (HEAVY_ITEM_DAMAGE.containsKey(item)) {
			HeavyItemDamage heavyItemDamage = HEAVY_ITEM_DAMAGE.get(item);
			float maxStackSize = stack.getMaxStackSize();
			return Mth.lerp(Math.min(maxStackSize, (float)stack.getCount() / maxStackSize), heavyItemDamage.startDamage, heavyItemDamage.maxStackDamage);
		}
		return 1.0F;
	}

	private static class HeavyItemDamage {
		private final float startDamage;
		private final float maxStackDamage;

		private HeavyItemDamage(float startDamage, float maxStackDamage) {
			this.startDamage = startDamage;
			this.maxStackDamage = maxStackDamage;
		}
	}
}
