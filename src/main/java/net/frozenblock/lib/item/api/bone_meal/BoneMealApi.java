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

package net.frozenblock.lib.item.api.bone_meal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class BoneMealApi {
	private static final Map<Block, BoneMealBehavior> BONE_MEAL_BEHAVIORS = new Object2ObjectOpenHashMap<>();

	/**
	 * Registers a {@link BoneMealBehavior}.
	 * @param block The {@link Block} to register the behavior for.
	 * @param boneMealBehavior The {@link BoneMealBehavior} to be registered.
	 */
	public static void register(Block block, BoneMealBehavior boneMealBehavior) {
		BONE_MEAL_BEHAVIORS.put(block, boneMealBehavior);
	}

	@ApiStatus.Internal
	@Nullable
	public static BoneMealApi.BoneMealBehavior get(Block block) {
		return BONE_MEAL_BEHAVIORS.getOrDefault(block, null);
	}

	public interface BoneMealBehavior {
		/**
		 * @param pos The {@link BlockPos} of the Bone Mealed block.
		 * @param state The {@link BlockState} of the Bone Mealed block.
		 * @return Whether the block can be Bone Mealed.
		 */
		boolean meetsRequirements(LevelReader level, BlockPos pos, BlockState state);

		/**
		 * @param pos The {@link BlockPos} of the Bone Mealed block.
		 * @param state The {@link BlockState} of the Bone Mealed block.
		 * @return Whether the usage of Bone Meal will trigger an event. If false, the Bone Meal is still consumed but nothing happens.
		 */
		default boolean isBoneMealSuccess(LevelReader level, RandomSource random, BlockPos pos, BlockState state) {
			return true;
		}

		/**
		 * Runs if both {@link #meetsRequirements(LevelReader, BlockPos, BlockState)} and {@link #isBoneMealSuccess(LevelReader, RandomSource, BlockPos, BlockState)} return {@code true}.
		 * @param pos The {@link BlockPos} of the Bone Mealed block.
		 * @param state The {@link BlockState} of the Bone Mealed block.
		 */
		void performBoneMeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state);

		/**
		 * @param state The {@link BlockState} being Bone Mealed.
		 * @param pos The {@link BlockPos} of the block being Bone Mealed.
		 * @return The {@link BlockPos} to create Bone Meal particles at.
		 */
		default BlockPos getParticlePos(BlockState state, BlockPos pos) {
			return pos;
		}

		/**
		 * @return Whether this results in blocks being placed on neighboring blocks.
		 * <p>
		 * If true, Bone Meal particles will be created in a small area around the Bone Mealed block.
		 */
		default boolean isNeighborSpreader() {
			return false;
		}
	}

}
