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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CooldownChange {

	public static void changeCooldown(@NotNull Player player, ItemStack item, int additionalCooldown, int min) {
		final ItemCooldowns manager = player.getCooldowns();
		final Identifier cooldownGroup = manager.getCooldownGroup(item);
		final ItemCooldowns.CooldownInstance entry = manager.cooldowns.get(cooldownGroup);
		if (entry == null) return;

		final int between = entry.endTime - entry.startTime;
		if ((between + additionalCooldown) <= min) return;
		((CooldownInterface)player.getCooldowns()).frozenLib$changeCooldown(cooldownGroup, additionalCooldown);
	}

}
