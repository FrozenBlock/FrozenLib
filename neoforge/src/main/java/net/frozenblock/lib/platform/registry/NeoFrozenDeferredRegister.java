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

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoFrozenDeferredRegister<T> implements FrozenDeferredRegister<T> {

	private final DeferredRegister<T> inner;
	private final Map<FrozenHolder<Object, Object>, Consumer<Object>> consumers = new Object2ObjectLinkedOpenHashMap<>();

	public NeoFrozenDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.inner = DeferredRegister.create(registryKey, namespace);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier) {
		return new NeoFrozenHolder<>(this.inner.register(name, supplier));
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
		var holder = new NeoFrozenHolder<>(this.inner.register(name, supplier));
		consumers.put((FrozenHolder) holder, (Consumer) also);
		return (FrozenHolder<T, I>) holder;
	}

	@Override
	public void register() {
		var bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		this.inner.register(bus);
		bus.addListener(this::runCallbacks);
	}

	private void runCallbacks(RegisterEvent event) {
		if (!event.getRegistryKey().equals(this.inner.getRegistryKey())) {
			return;
		}

		for (var consumer : consumers.entrySet()) {
			consumer.getValue().accept(consumer.getKey().get());
		}
	}
}
