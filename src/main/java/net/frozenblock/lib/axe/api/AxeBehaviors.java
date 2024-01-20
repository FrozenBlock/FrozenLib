/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.axe.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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

	@FunctionalInterface
	public interface AxeBehavior {
		boolean axe(UseOnContext context, Level world, BlockPos pos, BlockState state, Direction face, Direction horizontal);
	}

}
