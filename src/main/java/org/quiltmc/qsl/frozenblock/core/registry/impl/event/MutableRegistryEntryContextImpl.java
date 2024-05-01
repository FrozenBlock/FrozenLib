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

package org.quiltmc.qsl.frozenblock.core.registry.impl.event;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEntryContext;

/**
 * The default implementation for {@link RegistryEntryContext}.
 * <p>
 * In order to minimize allocations during event invocation, especially during registry iteration, this class is
 * mutable. The api interface only allows accessing fields of the class, whereas modification methods are reserved for the
 * impl.
 * <p>
 * Modified to work on Fabric
 *
 * @param <V> the type of the relevant {@link Registry}'s entries
 */
@ApiStatus.Internal
public class MutableRegistryEntryContextImpl<V> implements RegistryEntryContext<V> {
	private final Registry<V> registry;
	private V value;
	private ResourceLocation resourceLocation;
	private int raw = -1;

	public MutableRegistryEntryContextImpl(Registry<V> registry) {
		this.registry = registry;
	}

	/**
	 * Changes the current entry information.
	 * <p>
	 * Raw identifier is set to -1 to signify that it should be lazily looked up.
	 *
	 * @param id    the namespaced identifier of the new entry
	 * @param entry the new entry's object
	 */
	public void set(ResourceLocation id, V entry) {
		this.set(id, entry, -1);
	}

	/**
	 * Changes the current entry information.
	 *
	 * @param id    the namespaced identifier of the new entry
	 * @param entry the new entry's object
	 * @param rawId the raw int identifier of the new entry
	 */
	public void set(ResourceLocation id, V entry, int rawId) {
		this.resourceLocation = id;
		this.value = entry;
		this.raw = rawId;
	}

	@Override
	public Registry<V> registry() {
		return this.registry;
	}

	@Override
	public V value() {
		return this.value;
	}

	@Override
	public ResourceLocation resourceLocation() {
		return this.resourceLocation;
	}

	@Override
	public int rawId() {
		if (this.raw < 0) {
			this.raw = this.registry.getId(this.value);
		}

		return this.raw;
	}
}

