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

package net.frozenblock.lib.menu.api;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.resources.Identifier;

@UtilityClass
@ClientOnly
public final class SplashTextEvents {
	/**
	 * Enables the addition of new files to grab splash texts from.
	 * <p>
	 * Runs before {@link #ADD} and {@link #REMOVE}.
	 */
	public static final Event<AddSourceFiles> ADD_SOURCE_FILES = EventRegistry.createEnvironmentEvent(AddSourceFiles.class, callbacks -> (builder) -> {
		for (AddSourceFiles callback : callbacks) callback.collectSourceFileAdditions(builder);
	});

	/**
	 * Adds individual splash texts.
	 * <p>
	 * Runs before {@link #REMOVE} and after {@link #ADD_SOURCE_FILES}.
	 */
	public static final Event<Add> ADD = EventRegistry.createEnvironmentEvent(Add.class, callbacks -> (builder) -> {
		for (Add callback : callbacks) callback.collectAdditions(builder);
	});

	/**
	 * Removes individual splash texts.
	 * <p>
	 * Runs after {@link #ADD_SOURCE_FILES} and {@link #ADD}.
	 */
	public static final Event<Remove> REMOVE = EventRegistry.createEnvironmentEvent(Remove.class, callbacks -> (builder) -> {
		for (Remove callback : callbacks) callback.collectRemovals(builder);
	});

	@FunctionalInterface
	public interface AddSourceFiles {
		void collectSourceFileAdditions(ImmutableList.Builder<Identifier> sourceFiles);
	}

	@FunctionalInterface
	public interface Add {
		void collectAdditions(ImmutableList.Builder<String> additions);
	}

	@FunctionalInterface
	public interface Remove {
		void collectRemovals(ImmutableList.Builder<String> removals);
	}
}
