/*
 * Copyright 2022-2023 FrozenBlock
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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HeavyItemDamageRegistry {

	private static final Object2ObjectOpenHashMap<Item, HeavyItemDamage> HEAVY_ITEM_DAMAGE = new Object2ObjectOpenHashMap<>();

	public static void register(Item item, float startDamage, float maxStackDamage) {
		HEAVY_ITEM_DAMAGE.put(item, new HeavyItemDamage(startDamage, maxStackDamage));
	}

	public static float getDamage(ItemStack stack) {
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
