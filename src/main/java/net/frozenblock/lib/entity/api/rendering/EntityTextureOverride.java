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
