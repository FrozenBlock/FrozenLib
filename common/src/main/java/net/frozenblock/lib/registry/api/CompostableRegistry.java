/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.registry.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

@UtilityClass
public class CompostableRegistry {

	public static void add(ItemLike item, float chance) {
		FrozenLibInitPlatformUtils.COMPOSTABLE_REGISTRY.add(item, chance);
	}
}
