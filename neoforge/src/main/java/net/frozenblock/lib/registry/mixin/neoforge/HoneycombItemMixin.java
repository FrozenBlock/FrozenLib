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

package net.frozenblock.lib.registry.mixin.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HoneycombItem.class, priority = 1020)
public class HoneycombItemMixin {

	@ModifyReturnValue(method = "lambda$static$0", at = @At("RETURN"))
	private static BiMap frozenLib$mutableWaxablesMap(BiMap original) {
		return HashBiMap.create(original);
	}
}
