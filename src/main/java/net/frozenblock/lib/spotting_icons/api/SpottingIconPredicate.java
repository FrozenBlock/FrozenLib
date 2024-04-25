/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
