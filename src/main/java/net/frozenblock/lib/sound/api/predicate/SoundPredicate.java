/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.api.predicate;

import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class SoundPredicate<T extends Entity> {
	public static final ResourceLocation DEFAULT_ID = FrozenSharedConstants.id("default");
	public static final ResourceLocation NOT_SILENT_AND_ALIVE_ID = FrozenSharedConstants.id("not_silent_and_alive");

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
