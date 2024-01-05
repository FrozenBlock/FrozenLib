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

package net.frozenblock.lib.spotting_icons.api;

import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpottingIconPredicate<T extends Entity> {

    public static <T extends Entity> void register(ResourceLocation id, IconPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SPOTTING_ICON_PREDICATE, id, new SpottingIconPredicate<>(predicate));
    }

	private final IconPredicate<T> predicate;

	public SpottingIconPredicate(IconPredicate<T> predicate) {
		this.predicate = predicate;
	}

	@SuppressWarnings("unchecked")
    public static <T extends Entity> IconPredicate<T> getPredicate(@Nullable ResourceLocation id) {
        if (id != null) {
            if (FrozenRegistry.SPOTTING_ICON_PREDICATE.containsKey(id)) {
				SpottingIconPredicate<T> predicate = (SpottingIconPredicate<T>) FrozenRegistry.SPOTTING_ICON_PREDICATE.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			}
			FrozenSharedConstants.LOGGER.error("Unable to find spotting icon predicate " + id + "! Using default spotting icon predicate instead!");
        }
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

	@NotNull
	@Contract(pure = true)
	public static <T extends Entity> IconPredicate<T> defaultPredicate() {
		return Entity::isAlive;
	}

    public static ResourceLocation DEFAULT_ID = FrozenSharedConstants.id("default");

    public static void init() {
        register(DEFAULT_ID, defaultPredicate());
    }
}
