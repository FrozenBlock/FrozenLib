/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
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
 */

package org.quiltmc.qsl.frozenblock.core.registry.api.event;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the {@link RegistryAccess} setup context provided in the {@link RegistryEvents#DYNAMIC_REGISTRY_SETUP} event.
 * <p>
 * Modified to work on Fabric
 */
@ApiStatus.NonExtendable
public interface DynamicRegistryManagerSetupContext {
	/**
	 * {@return the registry access that is being currently setup}
	 */
	@Contract(pure = true)
	RegistryAccess registryManager();

	/**
	 * Attempts to safely register a game object into the given registry.
	 * <p>
	 * This method is preferred instead of {@link Registry#register(Registry, Identifier, Object)}
	 * as it makes sure to not overwrite data-pack-provided entries, it also makes sure the registry exists.
	 *
	 * @param key The key of the registry to register into
	 * @param id The identifier of the game object to register
	 * @param gameObjectSupplier The supplier of the game object to register
	 * @param <V> The type of game object to register
	 * @return the optional game object, if the registry is present then the optional is filled, or empty otherwise
	 */
	default <V> Optional<V> register(ResourceKey<? extends Registry<V>> key, Identifier id, Supplier<V> gameObjectSupplier) {
		return this.registryManager().lookup(key)
			.map(registry -> registry.containsKey(id) ? registry.getValue(id) : Registry.register(registry, id, gameObjectSupplier.get()));
	}

	/**
	 * Gets the registries requested by their keys.
	 * <p>
	 * If one of the queried registries isn't found, then this method will return {@code null}.
	 *
	 * @param keys The keys of the registries to get
	 * @return The registry map if all the queried registries have been found, or {@code null} otherwise
	 */
	@Contract(pure = true)
	@Nullable
	default RegistryMap getRegistries(Set<ResourceKey<? extends Registry<?>>> keys) {
		if (keys.isEmpty()) throw new IllegalArgumentException("Please provide at least one registry to gather.");

		Map<ResourceKey<? extends Registry<?>>, Registry<?>> foundRegistries = null;
		for (var key : keys) {
			final var possibleRegistry = this.registryManager().lookup(key);

			if (possibleRegistry.isEmpty()) continue;

			if (foundRegistries == null) foundRegistries = new Reference2ObjectOpenHashMap<>();
			foundRegistries.put(key, possibleRegistry.get());
		}

		if (foundRegistries == null || foundRegistries.size() != keys.size()) return null;
		return new RegistryMap(foundRegistries);
	}

	/**
	 * Executes the given action if all the provided registry keys are present in the {@link RegistryAccess}.
	 *
	 * @param action The action
	 * @param keys The registry keys to check
	 */
	default void withRegistries(Consumer<RegistryMap> action, Set<ResourceKey<? extends Registry<?>>> keys) {
		final var registries = this.getRegistries(keys);
		if (registries != null) action.accept(registries);
	}

	/**
	 * Attempts to create a new registry monitor for the given registry.
	 *
	 * @param key The key of the registry to monitor
	 * @param action The monitor callback
	 * @param <V> The type of values held in the registry
	 */
	default <V> void monitor(ResourceKey<? extends Registry<V>> key, Consumer<RegistryMonitor<V>> action) {
		this.registryManager().lookup(key).ifPresent(registry -> action.accept(RegistryMonitor.create(registry)));
	}

	/**
	 * Represents a map of known registries.
	 *
	 * @param registries the map of registries
	 */
	record RegistryMap(Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries) {
		/**
		 * Gets the registry from its key in this map.
		 *
		 * @param key The key of the registry
		 * @param <V> The type of values held in the registry
		 * @return The registry if present, or {@code null} otherwise
		 */
		@Contract(pure = true)
		@SuppressWarnings("unchecked")
		public <V> Registry<V> get(ResourceKey<? extends Registry<V>> key) {
			return (Registry<V>) this.registries.get(key);
		}

		/**
		 * Registers the given game object into the given registry.
		 *
		 * @param key The key of the registry to register into
		 * @param id The identifier of the game object to register
		 * @param gameObject The game object to register
		 * @param <V> The type of values held in the registry
		 * @return the game object
		 */
		public <V> V register(ResourceKey<? extends Registry<V>> key, Identifier id, V gameObject) {
			return Registry.register(this.get(key), id, gameObject);
		}
	}
}
