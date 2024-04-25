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

package net.frozenblock.lib.tag.api;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
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
public final class TagUtils {

	private TagUtils() {
		throw new UnsupportedOperationException("TagUtils contains only static declarations.");
	}


    @Nullable
    public static <T> T getRandomEntry(TagKey<T> tag) {
        return getRandomEntry(AdvancedMath.random(), tag);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getRandomEntry(RandomSource random, TagKey<T> tag) {
        Optional<? extends Registry<?>> maybeRegistry = BuiltInRegistries.REGISTRY.getOptional(tag.registry().location());
        Objects.requireNonNull(random);
        Objects.requireNonNull(tag);

        if (maybeRegistry.isPresent()) {
            Registry<T> registry = (Registry<T>) maybeRegistry.get();
            if (tag.isFor(registry.key())) {
                ArrayList<T> entries = new ArrayList<>();
                for (Holder<T> entry : registry.getTagOrEmpty(tag)) {
                    var optionalKey = entry.unwrapKey();
                    if (optionalKey.isPresent()) {
                        var key = optionalKey.get();
                        registry.getOptional(key).ifPresent(entries::add);
                    }
                }
                if (!entries.isEmpty()) {
                    return entries.get(random.nextInt(entries.size()));
                }
            }
        }
        return null;
    }

    public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
        return TagUtil.isIn(tagKey, entry);
    }

    public static <T> boolean isIn(@Nullable RegistryAccess registryAccess, TagKey<T> tagKey, T entry) {
        return TagUtil.isIn(registryAccess, tagKey, entry);
    }
}
