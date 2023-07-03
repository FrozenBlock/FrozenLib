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

package org.quiltmc.qsl.frozenblock.core.registry.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents the context implementation for the {@link RegistryEvents#DYNAMIC_REGISTRY_SETUP} event.
 * <p>
 * <b>It is imperative that the passed registries are mutable to allow registration.</b>
 *
 * @author LambdAurora
 */
@ApiStatus.Internal
public class DynamicRegistryManagerSetupContextImpl implements DynamicRegistryManagerSetupContext, RegistryAccess {
	private final ResourceManager resourceManager;
	private final Map<ResourceKey<?>, WritableRegistry<?>> registries;

	public DynamicRegistryManagerSetupContextImpl(ResourceManager resourceManager, Stream<WritableRegistry<?>> registries) {
		this.resourceManager = resourceManager;
		this.registries = new Object2ObjectOpenHashMap<>();

		registries.forEach(registry -> this.registries.put(registry.key(), registry));
	}

	@Override
	public @NotNull RegistryAccess registryManager() {
		return this;
	}

	@Override
	public @NotNull ResourceManager resourceManager() {
		return this.resourceManager;
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
