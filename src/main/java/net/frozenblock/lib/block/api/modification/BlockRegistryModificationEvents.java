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

package net.frozenblock.lib.block.api.modification;

import java.util.function.Function;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows for the modification of Blocks before they are registered.
 * <p>
 * These events should be referenced via the {@code frozenlib:events} entrypoint, or they may not work properly.
 */
@ApiStatus.Experimental
public class BlockRegistryModificationEvents {
	/**
	 * The event that is triggered when a Block is about to be created, letting its properties be modified.
	 * <p>
	 * Note that this is only called inside of {@link Blocks#register(ResourceKey, Function, BlockBehaviour.Properties)}.
	 * <p>
	 * Custom register methods will be ignored.
	 */
	public static final Event<ModifyProperties> MODIFY_PROPERTIES = FrozenEvents.createEnvironmentEvent(ModifyProperties.class, (callbacks) -> (id, properties) -> {
		for (var callback : callbacks) {
			final BlockBehaviour.Properties eventProperties = callback.modifyProperties(id, properties);
			if (eventProperties != null) properties = eventProperties;
		}
		return properties;
	});

	/**
	 * The event that is triggered when a Block is about to be created, letting its factory be modified.
	 * <p>
	 * Note that this is only called inside of {@link Blocks#register(ResourceKey, Function, BlockBehaviour.Properties)}.
	 * <p>
	 * Custom register methods will be ignored.
	 */
	public static final Event<ReplaceFactory> REPLACE_FACTORY = FrozenEvents.createEnvironmentEvent(ReplaceFactory.class, (callbacks) -> (id, properties, factory) -> {
		for (var callback : callbacks) {
			final Function<BlockBehaviour.Properties, Block> eventFactory = callback.replaceFactory(id, properties, factory);
			if (eventFactory != null) factory = eventFactory;
		}
		return factory;
	});

	/**
	 * A functional interface representing a modify properties event.
	 */
	@FunctionalInterface
	public interface ModifyProperties extends CommonEventEntrypoint {
		/**
		 * Triggers the event when a Block is about to be created, letting its properties be modified.
		 * @param id The {@link ResourceKey} of the Block.
		 * @param properties The {@link BlockBehaviour.Properties} of the Block.
		 * @return the modified {@link BlockBehaviour.Properties} to use.
		 */
		BlockBehaviour.Properties modifyProperties(ResourceKey<Block> id, BlockBehaviour.Properties properties);
	}

	/**
	 * A functional interface representing a replace factory event.
	 */
	@FunctionalInterface
	public interface ReplaceFactory extends CommonEventEntrypoint {
		/**
		 * Triggers the event when a Block is about to be created, letting its factory be replaced.
		 * @param id The {@link ResourceKey} of the Block.
		 * @param properties The {@link BlockBehaviour.Properties} of the Block.
		 * @param factory The {@link Function} that creates the Block.
		 * @return the new {@link Function} to use as the {@code factory}.
		 */
		Function<BlockBehaviour.Properties, Block> replaceFactory(ResourceKey<Block> id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, Block> factory);
	}
}
