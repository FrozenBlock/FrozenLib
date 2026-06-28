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

package net.frozenblock.lib.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.service.RegistryHelper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public class FabricRegistryHelper implements RegistryHelper {

	@Override
	public <T> FrozenDeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return new FabricFrozenDeferredRegister<>(registryKey, namespace);
	}

	@Override
	public <T> MappedRegistry<T> createSimpleRegistry(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		boolean synced,
		@Nullable RegistryBootstrap<T> bootstrap
	) {
		final var registry = new MappedRegistry<>(key, lifecycle, false);
		final var builder = FabricRegistryBuilder.from(registry);
		if (synced) builder.attribute(RegistryAttribute.SYNCED);
		final var registered = builder.buildAndRegister();
		if (bootstrap != null) bootstrap.run(registered);
		return registered;
	}

	@Override
	public <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		DynamicRegistries.register(key, directCodec);
	}

	@Override
	public <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		DynamicRegistries.registerSynced(key, directCodec);
	}

	@Override
	public <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec, Codec<T> networkCodec) {
		DynamicRegistries.registerSynced(key, directCodec, networkCodec);
	}
}
