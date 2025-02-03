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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Lets you add custom behaviors to run upon using an Axe on a block.
 */
public class AxeApi {
	private static final Map<Block, AxeBehavior> AXE_BEHAVIORS = new Object2ObjectOpenHashMap<>();

	/**
	 * Registers an {@link AxeBehavior} to a {@link Block}.
	 *
	 * @param block The {@link Block} to regsiter the {@link AxeBehavior} for.
	 * @param axeBehavior The {@link AxeBehavior} to register.
	 */
	public static void register(Block block, AxeBehavior axeBehavior) {
		AXE_BEHAVIORS.put(block, axeBehavior);
	}

	@ApiStatus.Internal
	@Nullable
	public static AxeBehavior get(Block block) {
		return AXE_BEHAVIORS.getOrDefault(block, null);
	}

	public interface AxeBehavior {
		/**
		 * Whether the {@link AxeBehavior} can trigger. If false, it is ignored and continues with vanilla behavior.
		 *
		 * @param level The current {@link Level}.
		 * @param pos The {@link BlockPos} of the block.
		 * @param direction The block face being interacted with.
		 * @param state The current {@link BlockState} of the block.
		 * @return whether this can trigger.
		 */
		boolean meetsRequirements(LevelReader level, BlockPos pos, Direction direction, BlockState state);

		/**
		 * Provides the finalized {@link BlockState} to be set after the interaction.
		 * <p>
		 * If null, will be ignored and continue with vanilla behavior.
		 * @param state The current {@link BlockState}.
		 * @return the finalized {@link BlockState} to be set after the interaction.
		 */
		BlockState getOutputBlockState(BlockState state);

		/**
		 * Custom behavior to be run if the {@link AxeBehavior} is successful.
		 * <p>
		 * This runs after the {@link BlockState} from {@link #getOutputBlockState(BlockState)} is set in the {@link Level}.
		 * @param level The current {@link Level}.
		 * @param pos The {@link BlockPos} of the block.
		 * @param direction The block face being interacted with.
		 * @param state The new {@link BlockState} of the block, provided by {@link #getOutputBlockState(BlockState)}.
		 * @param oldState The original {@link BlockState} of the block.
		 */
		void onSuccess(Level level, BlockPos pos, Direction direction, BlockState state, BlockState oldState);
	}

}
