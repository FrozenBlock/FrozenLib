/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.api.dripstone;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;

/**
 * An API related to Pointed Dripstone dripping.
 * <p>
 * If you are looking to add a custom funciton when a block is dripped ON, it is recommended to use {@link net.frozenblock.lib.block.api.tick.BlockScheduledTicks} and {@link #getDripstoneFluid(ServerLevel, BlockPos)}.
 */
public class DripstoneDripApi {
	private static final Map<Block, List<InjectedDrip>> WATER_DRIP_METHODS = new Object2ObjectOpenHashMap<>();
	private static final Map<Block, List<InjectedDrip>> LAVA_DRIP_METHODS = new Object2ObjectOpenHashMap<>();

	/**
	 * Returns the fluid dripping from Dripstone, if present, at this position.
	 *
	 * @param level The level to check in.
	 * @param pos   The position to check at.
	 * @return the fluid dripping from Dripstone, if present, at this position.
	 */
	public static Fluid getDripstoneFluid(ServerLevel level, BlockPos pos) {
		BlockPos blockPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, pos);
		if (blockPos == null) return Fluids.EMPTY;
		return PointedDripstoneBlock.getCauldronFillFluidType(level, blockPos);
	}

	/**
	 * Adds a custom method to be run upon a hanging Pointed Dripstone block dripping water from a specified block.
	 * <p>
	 * For example, using Blocks.SPONGE as the parameter would make this behavior run when water is dripped from Dripstone hanging from a Sponge.
	 *
	 * @param block        The block the Dripstone is hanging off.
	 * @param injectedDrip The custom behavior to add.
	 */
	public static void addWaterDrip(Block block, InjectedDrip injectedDrip) {
		if (WATER_DRIP_METHODS.containsKey(block)) {
			WATER_DRIP_METHODS.get(block).add(injectedDrip);
		} else {
			WATER_DRIP_METHODS.put(block, Lists.newArrayList(injectedDrip));
		}
	}

	@ApiStatus.Internal
	public static boolean containsWaterDrip(Block block) {
		return WATER_DRIP_METHODS.containsKey(block);
	}

	@ApiStatus.Internal
	public static void runWaterDripsIfPresent(
		Block block, ServerLevel world, BlockPos pos, PointedDripstoneBlock.FluidInfo fluidInfo
	) {
		if (WATER_DRIP_METHODS.containsKey(block)) {
			WATER_DRIP_METHODS.get(block).forEach(injectedDrip -> injectedDrip.onDrip(world, pos, fluidInfo));
		}
	}

	/**
	 * Adds a custom method to be run upon a hanging Pointed Dripstone block dripping lava from a specified block.
	 * <p>
	 * For example, using Blocks.SPONGE as the parameter would make this behavior run when lava is dripped from Dripstone hanging from a Sponge.
	 *
	 * @param block        The block the Dripstone is hanging off.
	 * @param injectedDrip The custom behavior to add.
	 */
	public static void addLavaDrip(Block block, InjectedDrip injectedDrip) {
		if (LAVA_DRIP_METHODS.containsKey(block)) {
			LAVA_DRIP_METHODS.get(block).add(injectedDrip);
		} else {
			LAVA_DRIP_METHODS.put(block, Lists.newArrayList(injectedDrip));
		}
	}

	@ApiStatus.Internal
	public static boolean containsLavaDrip(Block block) {
		return LAVA_DRIP_METHODS.containsKey(block);
	}

	@ApiStatus.Internal
	public static void runLavaDripsIfPresent(
		Block block, ServerLevel world, BlockPos pos, PointedDripstoneBlock.FluidInfo fluidInfo
	) {
		if (LAVA_DRIP_METHODS.containsKey(block)) {
			LAVA_DRIP_METHODS.get(block).forEach(injectedDrip -> injectedDrip.onDrip(world, pos, fluidInfo));
		}
	}

	@FunctionalInterface
	public interface InjectedDrip {
		void onDrip(ServerLevel world, BlockPos pos, PointedDripstoneBlock.FluidInfo fluidInfo);
	}
}
