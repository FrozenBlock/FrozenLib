/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.block.storage.api.hopper;

import java.util.ArrayList;
import lombok.experimental.UtilityClass;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@UtilityClass
public class HopperApi {
    private static final ArrayList<BlockEntityType<?>> BLACKLISTED_TYPES = new ArrayList<>();

	/**
	 * Adds a new {@link BlockEntityType} to blacklist from having interactions with Hoppers.
	 *
	 * @param type The {@link BlockEntityType} to blacklist.
	 */
	public static void addBlacklistedType(BlockEntityType<?> type) {
		if (!BLACKLISTED_TYPES.contains(type)) BLACKLISTED_TYPES.add(type);
	}

	/**
	 * Returns whether a {@link Container} is blacklisted from interacting with Hoppers.
	 *
	 * @param container The {@link Container} to check.
	 * @return whether the {@link Container} is blacklisted from interacting with Hoppers.
	 */
    public static boolean isContainerBlacklisted(Container container) {
        if (container instanceof BlockEntity block) return BLACKLISTED_TYPES.contains(block.getType());
        if (!(container instanceof CompoundContainer doubleInventory)) return false;
		if (doubleInventory.container1 instanceof BlockEntity block) {
			if (BLACKLISTED_TYPES.contains(block.getType())) return true;
		}
		if (doubleInventory.container2 instanceof BlockEntity block) return BLACKLISTED_TYPES.contains(block.getType());
        return false;
    }

}
