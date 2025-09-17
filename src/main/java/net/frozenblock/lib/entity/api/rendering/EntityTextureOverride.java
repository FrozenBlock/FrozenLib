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

package net.frozenblock.lib.entity.api.rendering;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.registry.client.FrozenLibClientRegistries;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import net.minecraft.ChatFormatting;

/**
 * Used to override an entity's texture if a condition is met.
 *
 * @param <T> The entity class the override is for.
 */
@Environment(EnvType.CLIENT)
public record EntityTextureOverride<T extends LivingEntity>(Class<? extends LivingEntityRenderer<?, ?, ?>> clazz, ResourceLocation texture, Condition condition) {

	/**
	 * Creates and registers an {@link EntityTextureOverride} based on an entity's name, not-case-sensitive.
	 *
	 * @param key The {@link ResourceLocation} to register the {@link EntityTextureOverride} to.
	 * @param clazz The {@link LivingEntityRenderer} class to register the {@link EntityTextureOverride} for.
	 * @param texture The texture to use while enabled.
	 * @param names Names that will cause the {@link EntityTextureOverride} to trigger.
	 * @return The created {@link EntityTextureOverride}.
	 */
	public static <T extends LivingEntity> @NotNull EntityTextureOverride<T> register(
		ResourceLocation key, Class<? extends LivingEntityRenderer<?, ?, ?>> clazz, ResourceLocation texture, String... names
	) {
		return register(key, clazz, texture, false, names);
	}

	/**
	 * Creates and registers an {@link EntityTextureOverride} based on an entity's name.
	 *
	 * @param key The {@link ResourceLocation} to register the {@link EntityTextureOverride} to.
	 * @param clazz The {@link LivingEntityRenderer} class to register the {@link EntityTextureOverride} for.
	 * @param texture The texture to use while enabled.
	 * @param caseSensitive Whether the texture override checks for the same case in the entity's name.
	 * @param names Names that will cause the {@link EntityTextureOverride} to trigger.
	 * @return The created {@link EntityTextureOverride}.
	 */
	public static <T extends LivingEntity> @NotNull EntityTextureOverride<T> register(
		ResourceLocation key, Class<? extends LivingEntityRenderer<?, ?, ?>> clazz, ResourceLocation texture, boolean caseSensitive, String... names
	) {
		if (texture == null) throw new IllegalArgumentException("Texture cannot be null!");

		return register(key, clazz, texture, renderState -> {
			if (renderState.nameTag == null) return false;
			
			String entityName = ChatFormatting.stripFormatting(renderState.nameTag.getString());
			AtomicBoolean isNameCorrect = new AtomicBoolean(false);
			if (names.length == 0) return true;

			Arrays.stream(names).toList().forEach(name -> {
				if (caseSensitive) {
					if (entityName.equalsIgnoreCase(name)) isNameCorrect.set(true);
				} else {
					if (entityName.equals(name)) isNameCorrect.set(true);
				}
			});
			return isNameCorrect.get();
		});
	}

	/**
	 * Creates and registers an {@link EntityTextureOverride}.
	 *
	 * @param key The {@link ResourceLocation} to register the {@link EntityTextureOverride} to.
	 * @param clazz The {@link LivingEntityRenderer} class to register the {@link EntityTextureOverride} for.
	 * @param texture The texture to use while enabled.
	 * @param condition The conditions to be met in order to override the entity's texture.
	 * @return The created {@link EntityTextureOverride}.
	 */
	public static <T extends LivingEntity> @NotNull EntityTextureOverride<T> register(
		ResourceLocation key, Class<? extends LivingEntityRenderer<?, ?, ?>> clazz, ResourceLocation texture, Condition condition
	) {
		return Registry.register(FrozenLibClientRegistries.ENTITY_TEXTURE_OVERRIDE, key, new EntityTextureOverride<>(clazz, texture, condition));
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface Condition {
		boolean test(LivingEntityRenderState entityRenderState);
	}
}
