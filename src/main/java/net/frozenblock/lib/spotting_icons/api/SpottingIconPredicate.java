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

package net.frozenblock.lib.spotting_icons.api;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class SpottingIconPredicate<T extends Entity> {

    public static <T extends Entity> void register(Identifier id, IconPredicate<T> predicate) {
		Registry.register(FrozenLibRegistries.SPOTTING_ICON_PREDICATE, id, new SpottingIconPredicate<>(predicate));
    }

	private final IconPredicate<T> predicate;

	public SpottingIconPredicate(IconPredicate<T> predicate) {
		this.predicate = predicate;
	}

	@SuppressWarnings("unchecked")
    public static <T extends Entity> IconPredicate<T> getPredicate(@Nullable Identifier id) {
        if (id == null) return defaultPredicate();

		if (FrozenLibRegistries.SPOTTING_ICON_PREDICATE.containsKey(id)) {
			final SpottingIconPredicate<T> predicate = (SpottingIconPredicate<T>) FrozenLibRegistries.SPOTTING_ICON_PREDICATE.getValue(id);
			if (predicate != null) return predicate.predicate;
		}

		FrozenLibConstants.LOGGER.error("Unable to find spotting icon predicate " + id + "! Using default spotting icon predicate instead!");
        return defaultPredicate();
    }

    @FunctionalInterface
    public interface IconPredicate<T extends Entity> {
        boolean test(T entity);

		default void onAdded(T entity) {
		}

		default void onRemoved(T entity) {
		}
    }

	@Contract(pure = true)
	public static <T extends Entity> IconPredicate<T> defaultPredicate() {
		return Entity::isAlive;
	}

    public static Identifier DEFAULT_ID = FrozenLibConstants.id("default");

    public static void init() {
        register(DEFAULT_ID, defaultPredicate());
    }
}
