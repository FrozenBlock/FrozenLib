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

package net.frozenblock.lib.platform.service;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface RegistryHelper {
	<T> FrozenDeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace);

	FrozenDeferredRegister.Items createDeferredItemsRegister(String namespace);

	FrozenDeferredRegister.Blocks createDeferredBlocksRegister(String namespace);

	FrozenDeferredRegister.DataComponents createDeferredDataComponentsRegister(String namespace);

	FrozenDeferredRegister.Entities createDeferredEntitiesRegister(String namespace);

	<T> MappedRegistry<T> createSimpleRegistry(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		boolean synced,
		@Nullable RegistryBootstrap<T> bootstrap
	);

	<T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec);

	<T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec);

	<T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec, Codec<T> networkCodec);

	@FunctionalInterface
	interface RegistryBootstrap<T> {
		void run(Registry<T> registry);
	}
}
