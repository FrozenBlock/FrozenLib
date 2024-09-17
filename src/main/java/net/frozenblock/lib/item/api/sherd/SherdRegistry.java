/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.item.api.sherd;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;

public class SherdRegistry {
	public static void register(Item sherd, ResourceLocation patternKey) {
		Object2ObjectLinkedOpenHashMap<Item, ResourceKey<DecoratedPotPattern>> newMap = new Object2ObjectLinkedOpenHashMap<>();
		newMap.putAll(DecoratedPotPatterns.ITEM_TO_POT_TEXTURE);
		newMap.put(sherd, ResourceKey.create(Registries.DECORATED_POT_PATTERN, patternKey));
		DecoratedPotPatterns.ITEM_TO_POT_TEXTURE = Map.copyOf(newMap);
	}
}
