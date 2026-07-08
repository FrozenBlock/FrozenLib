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

package net.frozenblock.lib.platform.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.service.RegistryHelper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

public class NeoRegistryHelper implements RegistryHelper {

	private static final List<Registry<?>> PENDING_REGISTRIES = new ArrayList<>();

	private record DynamicRegistryEntry<T>(ResourceKey<Registry<T>> key, Codec<T> codec, @Nullable Codec<T> networkCodec, boolean synced) {}
	private static final List<DynamicRegistryEntry<?>> PENDING_DYNAMIC_REGISTRIES = new ArrayList<>();

	@Override
	public <T> FrozenDeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return new NeoFrozenDeferredRegister<>(registryKey, namespace);
	}

	@Override
	public FrozenDeferredRegister.Items createDeferredItemsRegister(String namespace) {
		return new NeoFrozenDeferredRegister.Items(namespace);
	}

	@Override
	public FrozenDeferredRegister.Blocks createDeferredBlocksRegister(String namespace) {
		return new NeoFrozenDeferredRegister.Blocks(namespace);
	}

	@Override
	public FrozenDeferredRegister.DataComponents createDeferredDataComponentsRegister(String namespace) {
		return new NeoFrozenDeferredRegister.DataComponents(Registries.DATA_COMPONENT_TYPE, namespace);
	}

	@Override
	public FrozenDeferredRegister.Entities createDeferredEntitiesRegister(String namespace) {
		return new NeoFrozenDeferredRegister.Entities(namespace);
	}

	@Override
	public <T> MappedRegistry<T> createSimpleRegistry(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		boolean synced,
		@Nullable RegistryBootstrap<T> bootstrap
	) {
		var builder = new RegistryBuilder<>(key).disableRegistrationCheck();
		if (synced) builder.sync(true);
		var registry = (MappedRegistry<T>) builder.create();
		if (bootstrap != null) bootstrap.run(registry);
		PENDING_REGISTRIES.add(registry);
		return registry;
	}

	@Override
	public <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		PENDING_DYNAMIC_REGISTRIES.add(new DynamicRegistryEntry<>(key, directCodec, null, false));
	}

	@Override
	public <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		registerSyncedDynamicRegistry(key, directCodec, directCodec);
	}

	@Override
	public <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec, Codec<T> networkCodec) {
		PENDING_DYNAMIC_REGISTRIES.add(new DynamicRegistryEntry<>(key, directCodec, networkCodec, true));
	}

	public static void flushRegistries(NewRegistryEvent event) {
		for (Registry<?> registry : PENDING_REGISTRIES) event.register(registry);
		PENDING_REGISTRIES.clear();
	}

	@SuppressWarnings("unchecked")
	public static void flushDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
		for (DynamicRegistryEntry<?> entry : PENDING_DYNAMIC_REGISTRIES) {
			var typedEntry = (DynamicRegistryEntry<Object>) entry;
			if (typedEntry.synced()) {
				event.dataPackRegistry(typedEntry.key(), typedEntry.codec(), typedEntry.networkCodec());
			} else {
				event.dataPackRegistry(typedEntry.key(), typedEntry.codec());
			}
		}
		PENDING_DYNAMIC_REGISTRIES.clear();
	}
}
