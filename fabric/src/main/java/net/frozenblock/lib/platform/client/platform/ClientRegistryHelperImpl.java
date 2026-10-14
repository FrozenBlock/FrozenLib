/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.platform.client.platform;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.client.registry.v0.FabricClientRegistryBuilder;
import net.frozenblock.lib.platform.FabricDeferredRegister;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

@ClientOnly
public final class ClientRegistryHelperImpl {

	public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return new FabricDeferredRegister<>(BuiltInClientRegistries.REGISTRY, registryKey, namespace);
	}

	public static <T> MappedRegistry<T> create(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		@Nullable RegistryHelper.RegistryBootstrap<T> bootstrap
	) {
		final MappedRegistry<T> registry = new MappedRegistry<>(key, lifecycle, false);
		final FabricClientRegistryBuilder<T, MappedRegistry<T>> builder = FabricClientRegistryBuilder.from(registry);

		final MappedRegistry<T> registered = builder.buildAndRegister();
		if (bootstrap != null) bootstrap.run(registered);

		return registered;
	}
}
