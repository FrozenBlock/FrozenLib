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

import net.frozenblock.lib.item.impl.CooldownInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.jetbrains.annotations.NotNull;

public final class CooldownChange {

	private CooldownChange() {
		throw new UnsupportedOperationException("CooldownChange contains only static declarations.");
	}

	public static void changeCooldown(@NotNull Player player, Item item, int additionalCooldown, int min) {
		ItemCooldowns manager = player.getCooldowns();
		ItemCooldowns.CooldownInstance entry = manager.cooldowns.get(item);
		if (entry != null) {
			int between = entry.endTime - entry.startTime;
			if ((between + additionalCooldown) > min) {
				((CooldownInterface)player.getCooldowns()).changeCooldown(item, additionalCooldown);
			}
		}
	}

}
