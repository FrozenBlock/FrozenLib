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

package net.frozenblock.lib.block.api.dispenser;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/**
 * Lets you add custom behaviors to run upon a Dispenser using Shears.
 */
public final class ShearsDispenseItemBehaviorApi {
	private static final List<AdditionalBehavior> ADDITIONAL_BEHAVIORS = new ArrayList<>();

	/**
	 * Registers a new {@link AdditionalBehavior}.
	 *
	 * @param additionalBehavior The {@link AdditionalBehavior} to register.
	 */
	public static void register(AdditionalBehavior additionalBehavior) {
		ADDITIONAL_BEHAVIORS.add(additionalBehavior);
	}

	@ApiStatus.Internal
	public static boolean tryShear(BlockState state, ServerLevel level, ItemStack shears, BlockPos pos) {
		return ADDITIONAL_BEHAVIORS.stream().anyMatch(behavior -> behavior.tryShear(state, level, shears, pos));
	}

	@FunctionalInterface
	public interface AdditionalBehavior {
		/**
		 * Tries to shear a block at the given position.
		 * @param state The {@link BlockState} of the block.
		 * @param level The current {@link ServerLevel}.
		 * @param shears The {@link ItemStack} instance of the Shears.
		 * @param pos The {@link BlockPos} of the block.
		 * @return True if the shearing was successful. If true, no other behaviors will be tried.
		 */
		boolean tryShear(BlockState state, ServerLevel level, ItemStack shears, BlockPos pos);
	}
}
