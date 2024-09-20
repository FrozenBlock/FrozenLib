/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.item.api.axe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AxeBehaviors {
	private static final Map<Block, AxeBehavior> AXE_BEHAVIORS = new Object2ObjectOpenHashMap<>();

	public static void register(Block block, AxeBehavior axeBehavior) {
		AXE_BEHAVIORS.put(block, axeBehavior);
	}

	@Nullable
	public static AxeBehavior get(Block block) {
		return AXE_BEHAVIORS.getOrDefault(block, null);
	}

	public interface AxeBehavior {
		boolean meetsRequirements(LevelReader level, BlockPos pos, Direction direction, BlockState state);

		BlockState getOutputBlockState(BlockState state);

		void onSuccess(Level level, BlockPos pos, Direction direction, BlockState state, BlockState oldState);
	}

}
