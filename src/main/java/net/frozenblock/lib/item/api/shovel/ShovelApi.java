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
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ShovelApi {
	private static final Map<Block, ShovelBehavior> SHOVEL_BEHAVIORS = new Object2ObjectOpenHashMap<>();

	/**
	 * Registers a {@link ShovelBehavior}.
	 * @param block The {@link Block} to register the behavior for.
	 * @param shovelBehavior The {@link ShovelBehavior} to be registered.
	 */
	public static void register(Block block, ShovelBehavior shovelBehavior) {
		SHOVEL_BEHAVIORS.put(block, shovelBehavior);
	}

	@ApiStatus.Internal
	@Nullable
	public static ShovelBehavior get(Block block) {
		return SHOVEL_BEHAVIORS.getOrDefault(block, null);
	}

	public interface ShovelBehavior {
		/**
		 * @param pos The {@link BlockPos} of the block.
		 * @param state The {@link BlockState} of the block.
		 * @return Whether this behavior can be run.
		 */
		boolean meetsRequirements(LevelReader level, BlockPos pos, Direction direction, BlockState state);

		/**
		 * @param state The current {@link BlockState} of the block.
		 * @return The {@link BlockState} to be set in the level.
		 */
		BlockState getOutputBlockState(BlockState state);

		/**
		 * Runs any behavior on success.
		 * @param pos The {@link BlockPos} of the block.
		 * @param direction The clicked face of the block.
		 * @param state The new {@link BlockState} of the block.
		 * @param oldState The old {@link BlockState} of the block, before the shovel was used.
		 */
		void onSuccess(Level level, BlockPos pos, Direction direction, BlockState state, BlockState oldState);
	}

}
