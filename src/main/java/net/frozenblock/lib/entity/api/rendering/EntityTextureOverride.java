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

package net.frozenblock.lib.entity.api.rendering;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.registry.api.client.FrozenClientRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Used to override an entity's texture if a condition is met.
 *
 * @param <T> The entity class the override is for.
 */
@Environment(EnvType.CLIENT)
public record EntityTextureOverride<T extends LivingEntity>(EntityType<T> type, ResourceLocation texture, Condition<T> condition) {

	public static <T extends LivingEntity> EntityTextureOverride<T> register(ResourceLocation key, EntityType<T> type, ResourceLocation texture, String... names) {
		return register(key, type, texture, false, names);
	}

	public static <T extends LivingEntity> EntityTextureOverride<T> register(ResourceLocation key, EntityType<T> type, ResourceLocation texture, boolean caseSensitive, String... names) {
		return register(key, type, texture, entity -> {
			String entityName = ChatFormatting.stripFormatting(entity.getName().getString());
			AtomicBoolean isNameCorrect = new AtomicBoolean(false);
			if (names.length == 0) {
				return true;
			} else {
				Arrays.stream(names).toList().forEach(name -> {
					if (entityName != null) {
						if (caseSensitive) {
							if (entityName.equalsIgnoreCase(name)) {
								isNameCorrect.set(true);
							}
						} else {
							if (entityName.equals(name)) {
								isNameCorrect.set(true);
							}
						}
					}
				});
			}
			return isNameCorrect.get();
		});
	}

	public static <T extends LivingEntity> EntityTextureOverride<T> register(ResourceLocation key, EntityType<T> type, ResourceLocation texture, Condition<T> condition) {
		return Registry.register(FrozenClientRegistry.ENTITY_TEXTURE_OVERRIDE, key, new EntityTextureOverride<>(type, texture, condition));
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface Condition<T extends LivingEntity> {
		boolean condition(T entity);
	}
}
