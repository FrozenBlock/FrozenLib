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

package net.frozenblock.lib.tag.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * Contains methods related to {@link TagKey}s.
 */
@UtilityClass
public class TagUtils {

	public static <T> List<T> getAllEntries(TagKey<T> tag) {
		final Optional<? extends Registry<?>> optionalRegistry = BuiltInRegistries.REGISTRY.getOptional(tag.registry().identifier());
		if (optionalRegistry.isEmpty()) return List.of();

		final Registry<T> registry = (Registry<T>) optionalRegistry.get();
		if (!tag.isFor(registry.key())) return List.of();

		final ArrayList<T> entries = new ArrayList<>();
		for (Holder<T> entry : registry.getTagOrEmpty(tag)) {
			final var optionalKey = entry.unwrapKey();
			if (optionalKey.isEmpty()) continue;

			final var key = optionalKey.get();
			registry.getOptional(key).ifPresent(entries::add);
		}

		if (!entries.isEmpty()) return entries;
		return List.of();
	}

    @Nullable
    public static <T> T getRandomEntry(TagKey<T> tag) {
        return getRandomEntry(AdvancedMath.random(), tag);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getRandomEntry(RandomSource random, TagKey<T> tag) {
		final Optional<? extends Registry<?>> optionalRegistry = BuiltInRegistries.REGISTRY.getOptional(tag.registry().identifier());
        Objects.requireNonNull(random);
        Objects.requireNonNull(tag);

        if (optionalRegistry.isEmpty()) return null;

		final Registry<T> registry = (Registry<T>) optionalRegistry.get();
		if (!tag.isFor(registry.key())) return null;

		final ArrayList<T> entries = new ArrayList<>();
		for (Holder<T> entry : registry.getTagOrEmpty(tag)) {
			final var optionalKey = entry.unwrapKey();
			if (optionalKey.isEmpty()) continue;

			final var key = optionalKey.get();
			registry.getOptional(key).ifPresent(entries::add);
		}

		if (!entries.isEmpty()) return entries.get(random.nextInt(entries.size()));
        return null;
    }

    public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
        return TagUtil.isIn(tagKey, entry);
    }

    public static <T> boolean isIn(@Nullable RegistryAccess registryAccess, TagKey<T> tagKey, T entry) {
        return TagUtil.isIn(registryAccess, tagKey, entry);
    }
}
