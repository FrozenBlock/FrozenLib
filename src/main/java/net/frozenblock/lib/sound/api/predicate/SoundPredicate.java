/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.api.predicate;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class SoundPredicate<T extends Entity> {

    public static <T extends Entity> void register(ResourceLocation id, LoopPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE_SYNCED, id, new SoundPredicate<>(predicate));
    }

	public static <T extends Entity> void registerUnsynced(ResourceLocation id, LoopPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE, id, new SoundPredicate<>(predicate));
	}

	private final LoopPredicate<T> predicate;

	public SoundPredicate(LoopPredicate<T> predicate) {
		this.predicate = predicate;
	}

	@SuppressWarnings("unchecked")
    public static <T extends Entity> LoopPredicate<T> getPredicate(@Nullable ResourceLocation id) {
        if (id != null) {
            if (FrozenRegistry.SOUND_PREDICATE_SYNCED.containsKey(id)) {
				SoundPredicate<T> predicate = FrozenRegistry.SOUND_PREDICATE_SYNCED.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			} else if (FrozenRegistry.SOUND_PREDICATE.containsKey(id)) {
				SoundPredicate<T> predicate = FrozenRegistry.SOUND_PREDICATE.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			}
			FrozenMain.LOGGER.error("Unable to find sound predicate " + id + "! Using default sound predicate instead!");
        }
        return defaultPredicate();
    }

    @FunctionalInterface
    public interface LoopPredicate<T extends Entity> {
        boolean test(T entity);

		default void onStart(@Nullable T entity) {
		}

		default void onStop(@Nullable T entity) {
		}
    }

	public static <T extends Entity> LoopPredicate<T> defaultPredicate() {
		return entity -> !entity.isSilent();
	}
    public static ResourceLocation DEFAULT_ID = FrozenMain.id("default");
	public static <T extends Entity> LoopPredicate<T> notSilentAndAlive() {
		return entity -> !entity.isSilent();
	}
    public static ResourceLocation NOT_SILENT_AND_ALIVE_ID = FrozenMain.id("not_silent_and_alive");

    public static void init() {
        register(DEFAULT_ID, defaultPredicate());
        register(NOT_SILENT_AND_ALIVE_ID, notSilentAndAlive());
    }
}
