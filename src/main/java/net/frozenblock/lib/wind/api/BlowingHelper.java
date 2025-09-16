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

package net.frozenblock.lib.wind.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BlowingHelper {

	public static boolean canBlowingPassThrough(LevelAccessor level, BlockPos pos, @NotNull BlockState state, @NotNull Direction direction) {
		return !((state.isFaceSturdy(level, pos, direction.getOpposite(), SupportType.CENTER)
			&& !state.is(FrozenBlockTags.BLOWING_CAN_PASS_THROUGH))
			|| state.is(FrozenBlockTags.BLOWING_CANNOT_PASS_THROUGH));
	}

}
