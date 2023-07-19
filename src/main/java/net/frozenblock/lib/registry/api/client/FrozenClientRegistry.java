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

package net.frozenblock.lib.registry.api.client;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.entity.api.rendering.EntityTextureOverride;
import net.frozenblock.lib.integration.api.client.ClientModIntegration;
import net.frozenblock.lib.integration.api.client.ClientModIntegrationSupplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

@Environment(EnvType.CLIENT)
public class FrozenClientRegistry {

	public static final MappedRegistry<EntityTextureOverride> ENTITY_TEXTURE_OVERRIDE = FabricRegistryBuilder.createSimple(EntityTextureOverride.class, FrozenMain.id("entity_texture_override"))
			.buildAndRegister();

	public static final ResourceKey<Registry<ClientModIntegrationSupplier<?>>> CLIENT_MOD_INTEGRATION_REGISTRY = ResourceKey.createRegistryKey(FrozenMain.id("client_mod_integration"));

	public static final MappedRegistry<ClientModIntegrationSupplier<?>> CLIENT_MOD_INTEGRATION = createSimple(CLIENT_MOD_INTEGRATION_REGISTRY, Lifecycle.stable(), null,
		registry -> Registry.register(registry, FrozenMain.id("dummy"), new ClientModIntegrationSupplier<>(() -> new ClientModIntegration("dummy") {
				@Override
				public void init() {}
			},
				"dummy"
			)
		)
	);

	private static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryAttribute attribute, BuiltInRegistries.RegistryBootstrap<T> bootstrap) {
		var registry = new MappedRegistry<>(key, lifecycle, false);
		var fabricRegistryBuilder = FabricRegistryBuilder.from(registry);

		if (attribute != null) {
			fabricRegistryBuilder.attribute(attribute);
		}

		var registeredRegistry = fabricRegistryBuilder.buildAndRegister();

		if (bootstrap != null) {
			bootstrap.run(registeredRegistry);
		}

		return registeredRegistry;
	}

	public static void initRegistry() {
		// NO-OP
		// This is just to make sure the registry is initialized.
	}
}
