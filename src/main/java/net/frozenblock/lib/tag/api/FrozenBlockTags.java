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

package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

@UtilityClass
public class FrozenBlockTags {
    public static final TagKey<Block> DRIPSTONE_CAN_DRIP_ON = bind("dripstone_can_drip");
	public static final TagKey<Block> BLOWING_CAN_PASS_THROUGH = bind("blowing_can_pass_through");
	public static final TagKey<Block> BLOWING_CANNOT_PASS_THROUGH = bind("blowing_cannot_pass_through");
	public static final TagKey<Block> STRUCTURE_PLACE_SCHEDULES_TICK = bind("structure_place_schedules_tick");

    private static TagKey<Block> bind(String path) {
        return TagKey.create(Registries.BLOCK, FrozenLibConstants.id(path));
    }
}
