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

package net.frozenblock.lib.registry.client;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entity.client.api.renderer.entity.EntityTextureOverride;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

@UtilityClass
@ClientOnly
public final class FrozenLibClientRegistries {
	public static final ResourceKey<Registry<EntityTextureOverride<?, ? extends LivingEntityRenderState>>> ENTITY_TEXTURE_OVERRIDE = createRegistryKey("entity_texture_override");

	static {
		FrozenLibBuiltInClientRegistries.init();
	}

	private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
		return ResourceKey.createRegistryKey(ClientRegistries.ROOT_REGISTRY_NAME, FrozenLibConstants.id(name));
	}
}
