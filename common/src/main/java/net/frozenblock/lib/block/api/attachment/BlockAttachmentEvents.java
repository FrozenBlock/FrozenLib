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

import java.util.function.BiConsumer;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.impl.attachment.BlockAttachmentHolder;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

/**
 * Events pertaining to {@link BlockAttachmentHolder}.
 */
@UtilityClass
public final class BlockAttachmentEvents {
	/**
	 * The event that is triggered after {@link CommonLifecycleEvents#TAGS_LOADED} causes all {@link Block}s to clear their attached data.
	 */
	public static final Event<Register> REGISTER = EventRegistry.createEnvironmentEvent(Register.class,
		callbacks -> registries -> {
		for (var callback : callbacks) callback.register(registries);
	});

	/**
	 * The event that is triggered when data is attached to a {@link Block}.
	 */
	public static final Event<OnSet> ON_SET = EventRegistry.createEnvironmentEvent(OnSet.class,
		callbacks -> (block, key, value) -> {
		for (var callback : callbacks) callback.onSet(block, key, value);
	});

	@ApiStatus.Internal
	public static void init() {
		CommonLifecycleEvents.TAGS_LOADED.register(((registries, client) -> {
			BuiltInRegistries.BLOCK.forEach(BlockAttachmentHolder::frozenLib$clearAttachments);
			REGISTER.invoker().register(registries);
		}));
	}

	/**
	 * Runs a {@link BiConsumer} for all {@link Block}s in a given {@link TagKey Block Tag} upon {@link #REGISTER} being called.
	 * @param tag the {@link TagKey Block Tag} containing the {@link Block}s to run the {@link BiConsumer} on.
	 * @param callback the {@link BiConsumer} to run per-{@link Block}.
	 */
	public static void forAllInTag(TagKey<Block> tag, BiConsumer<Block, RegistryAccess> callback) {
		REGISTER.register(registries -> {
			registries.lookup(Registries.BLOCK).ifPresent(blocks -> {
				blocks.getTagOrEmpty(tag).forEach(block -> callback.accept(block.value(), registries));
			});
		});
	}

	/**
	 * A functional interface representing a Register event.
	 */
	@FunctionalInterface
	public interface Register extends CommonEventEntrypoint {
		/**
		 * Runs after {@link CommonLifecycleEvents#TAGS_LOADED} causes all {@link Block}s to clear their attached data.
		 * <p>
		 * Should only be used for setting up attached data for {@link Block}s.
		 * @param registries the current {@link RegistryAccess}.
		 */
		void register(RegistryAccess registries);
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
