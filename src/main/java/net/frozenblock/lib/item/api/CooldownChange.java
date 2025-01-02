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

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CooldownChange {

	public static void changeCooldown(@NotNull Player player, Item item, int additionalCooldown, int min) {
		ItemCooldowns manager = player.getCooldowns();
		ItemCooldowns.CooldownInstance entry = manager.cooldowns.get(item);
		if (entry != null) {
			int between = entry.endTime - entry.startTime;
			if ((between + additionalCooldown) > min) {
				((CooldownInterface)player.getCooldowns()).frozenLib$changeCooldown(item, additionalCooldown);
			}
		}
	}

}
