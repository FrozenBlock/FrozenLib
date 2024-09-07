/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.sound.api.predicate;

import java.util.function.Supplier;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SoundPredicate<T extends Entity> {
	public static final ResourceLocation DEFAULT_ID = FrozenSharedConstants.id("default");
	public static final ResourceLocation NOT_SILENT_AND_ALIVE_ID = FrozenSharedConstants.id("not_silent_and_alive");

	private final Supplier<LoopPredicate<T>> predicateSupplier;

    public static <T extends Entity> void register(ResourceLocation id, Supplier<LoopPredicate<T>> predicateSupplier) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE, id, new SoundPredicate<>(predicateSupplier));
    }

	public static <T extends Entity> void registerUnsynced(ResourceLocation id, Supplier<LoopPredicate<T>> predicateSupplier) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE_UNSYNCED, id, new SoundPredicate<>(predicateSupplier));
	}

	public SoundPredicate(Supplier<LoopPredicate<T>> predicateSupplier) {
		this.predicateSupplier = predicateSupplier;
	}

	@SuppressWarnings("unchecked")
    public static <T extends Entity> LoopPredicate<T> getPredicate(@Nullable ResourceLocation id) {
        if (id != null) {
            if (FrozenRegistry.SOUND_PREDICATE.containsKey(id)) {
				SoundPredicate<T> predicate = (SoundPredicate<T>) FrozenRegistry.SOUND_PREDICATE.getValue(id);
				if (predicate != null) {
					return predicate.predicateSupplier.get();
				}
			} else if (FrozenRegistry.SOUND_PREDICATE_UNSYNCED.containsKey(id)) {
				SoundPredicate<T> predicate = (SoundPredicate<T>) FrozenRegistry.SOUND_PREDICATE_UNSYNCED.getValue(id);
				if (predicate != null) {
					return predicate.predicateSupplier.get();
				}
			}
			FrozenSharedConstants.LOGGER.error("Unable to find sound predicate " + id + "! Using default sound predicate instead!");
        }
        return defaultPredicate();
    }

	@Contract(pure = true)
	public static <T extends Entity> @NotNull LoopPredicate<T> defaultPredicate() {
		return entity -> !entity.isSilent();
	}
	@Contract(pure = true)
	public static <T extends Entity> @NotNull LoopPredicate<T> notSilentAndAlive() {
		return entity -> !entity.isSilent();
	}

	public static void init() {
		register(DEFAULT_ID, SoundPredicate::defaultPredicate);
		register(NOT_SILENT_AND_ALIVE_ID, SoundPredicate::notSilentAndAlive);
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
