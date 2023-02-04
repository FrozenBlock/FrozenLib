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

package net.frozenblock.lib.tag.api;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class FrozenBlockTags {

	private FrozenBlockTags() {
		throw new UnsupportedOperationException("FrozenBlockTags contains only static declarations.");
	}

    public static final TagKey<Block> DRIPSTONE_CAN_DRIP_ON = bind("dripstone_can_drip");

	/**
	 * A tag for blocks that the player should be able to interact with whilst crouching
	 */
	public static final TagKey<Block> CAN_INTERACT_WHILE_CROUCHING = bind("can_interact_while_crouching");

    private static TagKey<Block> bind(String path) {
        return TagKey.create(Registries.BLOCK, FrozenMain.id(path));
    }
}
