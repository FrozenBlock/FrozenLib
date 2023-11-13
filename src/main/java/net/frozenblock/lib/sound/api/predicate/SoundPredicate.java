/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.sound.api.predicate;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class SoundPredicate<T extends Entity> {
	public static final ResourceLocation DEFAULT_ID = FrozenMain.id("default");
	public static final ResourceLocation NOT_SILENT_AND_ALIVE_ID = FrozenMain.id("not_silent_and_alive");

	private final LoopPredicate<T> predicate;

    public static <T extends Entity> void register(ResourceLocation id, LoopPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE, id, new SoundPredicate<>(predicate));
    }

	public static <T extends Entity> void registerUnsynced(ResourceLocation id, LoopPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE_UNSYNCED, id, new SoundPredicate<>(predicate));
	}

	public SoundPredicate(LoopPredicate<T> predicate) {
		this.predicate = predicate;
	}

	@SuppressWarnings("unchecked")
    public static <T extends Entity> LoopPredicate<T> getPredicate(@Nullable ResourceLocation id) {
        if (id != null) {
            if (FrozenRegistry.SOUND_PREDICATE.containsKey(id)) {
				SoundPredicate<T> predicate = (SoundPredicate<T>) FrozenRegistry.SOUND_PREDICATE.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			} else if (FrozenRegistry.SOUND_PREDICATE_UNSYNCED.containsKey(id)) {
				SoundPredicate<T> predicate = (SoundPredicate<T>) FrozenRegistry.SOUND_PREDICATE_UNSYNCED.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			}
			FrozenSharedConstants.LOGGER.error("Unable to find sound predicate " + id + "! Using default sound predicate instead!");
        }
        return defaultPredicate();
    }

	public static <T extends Entity> LoopPredicate<T> defaultPredicate() {
		return entity -> !entity.isSilent();
	}
	public static <T extends Entity> LoopPredicate<T> notSilentAndAlive() {
		return entity -> !entity.isSilent();
	}

	public static void init() {
		register(DEFAULT_ID, defaultPredicate());
		register(NOT_SILENT_AND_ALIVE_ID, notSilentAndAlive());
	}

    @FunctionalInterface
    public interface LoopPredicate<T extends Entity> {
        boolean test(T entity);

		@Nullable
		default Boolean firstTickTest(T entity) {
			return null;
		}

		default void onStart(@Nullable T entity) {
		}

		default void onStop(@Nullable T entity) {
		}
    }
}
