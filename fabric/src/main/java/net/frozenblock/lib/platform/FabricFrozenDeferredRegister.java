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

import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricFrozenDeferredRegister<T> implements FrozenDeferredRegister<T> {

	private final ResourceKey<? extends Registry<T>> registryKey;
	private final String namespace;
	private final List<PendingEntry<T, ?>> pending = new ArrayList<>();

	public FabricFrozenDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.registryKey = registryKey;
		this.namespace = namespace;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier) {
		return register(name, supplier, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
		FabricFrozenHolder<T, I> holder = new FabricFrozenHolder<>();
		this.pending.add(new PendingEntry<>(Identifier.fromNamespaceAndPath(this.namespace, name), supplier, also, holder));
		return holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier) {
		return register(key, supplier, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also) {
		FabricFrozenHolder<T, I> holder = new FabricFrozenHolder<>();
		this.pending.add(new PendingEntry<>(key.identifier(), supplier, also, holder));
		return holder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void register() {
		Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getOptional(this.registryKey.identifier())
			.orElseThrow(() -> new IllegalStateException("No registry found for key: " + this.registryKey.identifier()));
		for (PendingEntry<T, ?> entry : this.pending) {
			registerEntry(registry, entry);
		}
		this.pending.clear();
	}

	@SuppressWarnings("unchecked")
	private <I extends T> void registerEntry(Registry<T> registry, PendingEntry<T, I> entry) {
		ResourceKey<T> key = ResourceKey.create(this.registryKey, entry.id());
		I value = entry.supplier().get();
		Registry.register(registry, entry.id(), value);
		Holder.Reference<T> ref = registry.getOrThrow(key);
		entry.holder().bind((Holder.Reference<I>) ref);
		var also = entry.also();
		if (also != null)
			also.accept((I) ref.value());
	}

	private record PendingEntry<T, I extends T>(
		Identifier id,
		Supplier<? extends I> supplier,
		@Nullable Consumer<I> also,
		FabricFrozenHolder<T, I> holder
	) {}
}
