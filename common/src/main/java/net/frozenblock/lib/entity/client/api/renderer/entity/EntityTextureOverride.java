/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.entity.client.api.renderer.entity;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.frozenblock.lib.renderer.FrozenLibRenderStateDataKeys;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Used to override an entity's texture if a condition is met.
 *
 * @param <T> The entity class the override is for.
 */
@ClientOnly
public record EntityTextureOverride<T extends LivingEntity, S extends LivingEntityRenderState>(
	EntityType<T> entityType,
	Identifier texture,
	Predicate<S> condition
) {

	/**
	 * Creates an {@link EntityTextureOverride} based on an entity's name, not-case-sensitive.
	 *
	 * @param entityType The {@link EntityType} to register the {@link EntityTextureOverride} for.
	 * @param texture The texture to use while enabled.
	 * @param names Names that will cause the {@link EntityTextureOverride} to trigger.
	 * @return The created {@link EntityTextureOverride}.
	 */
	public static <T extends LivingEntity, S extends LivingEntityRenderState> EntityTextureOverride<T, S> create(
		EntityType<T> entityType,
		Identifier texture,
		String... names
	) {
		return create(entityType, texture, false, names);
	}

	/**
	 * Creates an {@link EntityTextureOverride} based on an entity's name.
	 *
	 * @param entityType The {@link EntityType} to register the {@link EntityTextureOverride} for.
	 * @param texture The texture to use while enabled.
	 * @param caseSensitive Whether the texture override checks for the same case in the entity's name.
	 * @param names Names that will cause the {@link EntityTextureOverride} to trigger.
	 * @return The created {@link EntityTextureOverride}.
	 */
	public static <T extends LivingEntity, S extends LivingEntityRenderState> EntityTextureOverride<T, S> create(
		EntityType<T> entityType,
		Identifier texture,
		boolean caseSensitive,
		String... names
	) {
		if (texture == null) throw new IllegalArgumentException("Texture cannot be null!");

		return create(entityType, texture, renderState -> {
			final String entityName = renderState.frozenLib$getData(FrozenLibRenderStateDataKeys.ENTITY_NAME);
			if (entityName == null) return false;

			final AtomicBoolean isNameCorrect = new AtomicBoolean(false);
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
	 * Creates an {@link EntityTextureOverride}.
	 *
	 * @param entityType The {@link EntityType} to register the {@link EntityTextureOverride} for.
	 * @param texture The texture to use while enabled.
	 * @param condition The conditions that must be met in order to override the entity's texture.
	 * @return The created {@link EntityTextureOverride}.
	 */
	public static <T extends LivingEntity, S extends LivingEntityRenderState> EntityTextureOverride<T, S> create(
		EntityType<T> entityType,
		Identifier texture,
		Predicate<S> condition
	) {
		return new EntityTextureOverride<>(entityType, texture, condition);
	}
}
