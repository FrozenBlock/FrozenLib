/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.event.api.events;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Implemented separately on Fabric and NeoForge.
 * <p>
 * Fabric: Redirects to {@code PlayerBlockBreakEvents}.
 * <p>
 * NeoForge: Copies Fabric's implementation via mixin, as NeoForge's {@code BreakBlockEvent} doesn't provide the same functionality and is called at a different time.
 * <p>
 * Unlike Fabric, these events are also called from the client. Be sure to use {@link Level#isClientSide()} to determine the environment as needed.
 */
@UtilityClass
public final class PlayerBlockBreakEvents {
	/**
	 * Callback before a block is broken.
	 * <p>
	 * If any listener cancels a block breaking action, that block breaking action is cancelled and {@link #CANCELED} event is fired.
	 * Otherwise, the {@link #AFTER} event is fired.</p>
	 */
	public static final Event<Before> BEFORE = EventRegistry.createEnvironmentEvent(Before.class, callbacks -> (level, player, pos, state, entity) -> {
		for (Before event : callbacks) {
			final boolean result = event.beforeBlockBreak(level, player, pos, state, entity);
			if (!result) return false;
		}
		return true;
	});

	/**
	 * Callback after a block is broken.
	 */
	public static final Event<After> AFTER = EventRegistry.createEnvironmentEvent(After.class, callbacks -> (level, player, pos, state, entity) -> {
		for (After event : callbacks) event.afterBlockBreak(level, player, pos, state, entity);
	});

	/**
	 * Callback when a block break has been canceled.
	 */
	public static final Event<Canceled> CANCELED = EventRegistry.createEnvironmentEvent(Canceled.class, callbacks -> (level, player, pos, state, entity) -> {
		for (Canceled event : callbacks) event.onBlockBreakCanceled(level, player, pos, state, entity);
	});

	@FunctionalInterface
	public interface Before {
		/**
		 * Called before a block is broken and allows cancelling the block breaking.
		 * <p>
		 * Implementations should not modify the level or assume the block break has completed or failed.
		 *
		 * @param level the level in which the block is broken
		 * @param player the player breaking the block
		 * @param pos the position at which the block is broken
		 * @param state the block state <strong>before</strong> the block is broken
		 * @param blockEntity the block entity <strong>before</strong> the block is broken, can be {@code null}
		 * @return {@code false} to cancel block breaking action, or {@code true} to pass to next listener
		 */
		boolean beforeBlockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
	}

	@FunctionalInterface
	public interface After {
		/**
		 * Called after a block is successfully broken.
		 *
		 * @param level the level where the block was broken
		 * @param player the player who broke the block
		 * @param pos the position where the block was broken
		 * @param state the block state <strong>before</strong> the block was broken
		 * @param blockEntity the block entity of the broken block, can be {@code null}
		 */
		void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
	}

	@FunctionalInterface
	public interface Canceled {
		/**
		 * Called when a block break has been canceled.
		 *
		 * @param level the level where the block was going to be broken
		 * @param player the player who was going to break the block
		 * @param pos the position where the block was going to be broken
		 * @param state the block state of the block that was going to be broken
		 * @param blockEntity the block entity of the block that was going to be broken, can be {@code null}
		 */
		void onBlockBreakCanceled(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
	}
}
