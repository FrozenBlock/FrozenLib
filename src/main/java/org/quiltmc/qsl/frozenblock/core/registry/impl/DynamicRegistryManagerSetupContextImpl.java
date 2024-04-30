/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.core.registry.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

/**
 * Represents the context implementation for the {@link RegistryEvents#DYNAMIC_REGISTRY_SETUP} event.
 * <p>
 * <b>It is imperative that the passed registries are mutable to allow registration.</b>
 *
 * @author LambdAurora
 */
@ApiStatus.Internal
public class DynamicRegistryManagerSetupContextImpl implements DynamicRegistryManagerSetupContext, RegistryAccess {
	private final Map<ResourceKey<?>, WritableRegistry<?>> registries;

	public DynamicRegistryManagerSetupContextImpl(Stream<WritableRegistry<?>> registries) {
		this.registries = new Object2ObjectOpenHashMap<>();

		registries.forEach(registry -> this.registries.put(registry.key(), registry));
	}

	@Override
	public @NotNull RegistryAccess registryManager() {
		return this;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@NotNull
	public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> key) {
		return Optional.ofNullable((Registry) this.registries.get(key)).map(registry -> registry);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@NotNull
	public Stream<RegistryEntry<?>> registries() {
		return this.registries.entrySet().stream().map(entry -> new RegistryEntry<>((ResourceKey) entry.getKey(), entry.getValue()));
	}
}
