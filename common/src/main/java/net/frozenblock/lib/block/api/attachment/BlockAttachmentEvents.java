/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.block.api.attachment;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.impl.attachment.BlockAttachmentHolder;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Events pertaining to {@link BlockAttachmentHolder}.
 */
@UtilityClass
public final class BlockAttachmentEvents {
	/**
	 * The event that is triggered when {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource) animateTick} is called.
	 */
	public static final Event<Register> REGISTER = EventRegistry.createEnvironmentEvent(Register.class,
		callbacks -> (block, registries) -> {
		for (var callback : callbacks) callback.register(block, registries);
	});

	/**
	 * The event that is triggered when data is attached to a {@link Block}.
	 */
	public static final Event<OnSet> ON_SET = EventRegistry.createEnvironmentEvent(OnSet.class,
		callbacks -> (block, key, value) -> {
		for (var callback : callbacks) callback.onSet(block, key, value);
	});

	public static void init() {
		CommonLifecycleEvents.TAGS_LOADED.register(((registries, client) -> {
			BuiltInRegistries.BLOCK.forEach(block -> {
				block.frozenLib$clearAttachments();
				REGISTER.invoker().register(block, registries);
			});
		}));
	}

	/**
	 * A functional interface representing a Register event.
	 */
	@FunctionalInterface
	public interface Register extends CommonEventEntrypoint {
		/**
		 * Runs after {@link CommonLifecycleEvents#TAGS_LOADED} causes all {@link Block}s to clear their attached data.
		 * <p>
		 * Is invoked per-{@link Block}, and should only be used for setting up new attached data for said {@link Block}.
		 * @param block the {@link Block} to register attached data to.
		 * @param registries the current {@link RegistryAccess}.
		 */
		void register(Block block, RegistryAccess registries);
	}

	/**
	 * A functional interface representing an On Set event.
	 */
	@FunctionalInterface
	public interface OnSet extends CommonEventEntrypoint {
		/**
		 * Runs when data is attached to a {@link Block}.
		 * <p>
		 * This event should only be used if you absolutely know what you're doing.
		 * @param block the {@link Block} the data was attached to.
		 * @param key the {@link BlockAttachmentKey} the data was attached with.
		 * @param value the value that was attached to the {@link Block}.
		 */
		void onSet(Block block, BlockAttachmentKey<?> key, Object value);
	}
}
