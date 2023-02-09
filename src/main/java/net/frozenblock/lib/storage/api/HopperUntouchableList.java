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

package net.frozenblock.lib.storage.api;

import java.util.ArrayList;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HopperUntouchableList {

    public static final ArrayList<BlockEntityType<?>> BLACKLISTED_TYPES = new ArrayList<>();

    public static boolean inventoryContainsBlacklisted(Container inventory) {
        if (inventory instanceof BlockEntity block) {
			return BLACKLISTED_TYPES.contains(block.getType());
        } else if (inventory instanceof CompoundContainer doubleInventory) {
            if (doubleInventory.container1 instanceof BlockEntity block) {
                if (BLACKLISTED_TYPES.contains(block.getType())) {
                    return true;
                }
            }
            if (doubleInventory.container2 instanceof BlockEntity block) {
				return BLACKLISTED_TYPES.contains(block.getType());
            }
        }
        return false;
    }

}
