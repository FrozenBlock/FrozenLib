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

package net.frozenblock.lib.item.api.shovel;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ShovelBehaviors {
	private static final Map<Block, ShovelBehavior> SHOVEL_BEHAVIORS = new Object2ObjectOpenHashMap<>();

	public static void register(Block block, ShovelBehavior shovelBehavior) {
		SHOVEL_BEHAVIORS.put(block, shovelBehavior);
	}

	@Nullable
	public static ShovelBehavior get(Block block) {
		return SHOVEL_BEHAVIORS.getOrDefault(block, null);
	}

	public interface ShovelBehavior {
		boolean meetsRequirements(LevelReader level, BlockPos pos, Direction direction, BlockState state);

		BlockState getOutputBlockState(BlockState state);

		void onSuccess(Level level, BlockPos pos, Direction direction, BlockState state, BlockState oldState);
	}

}
