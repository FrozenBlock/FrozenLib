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

package net.frozenblock.lib.platform.api.registry;

import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A cross-platform deferred registry abstraction, analogous to NeoForge's {@code DeferredRegister}.
 *
 * <p>Create with {@link #create(ResourceKey, String)}, queue entries with
 * {@link #register(String, Supplier)}, then call {@link #register()} during mod initialization.
 *
 * <p>On Fabric: {@link #register()} immediately registers all queued entries.
 * On NeoForge: {@link #register()} delegates to NeoForge's DeferredRegister and hooks
 * the active mod's event bus via {@code ModLoadingContext}. Must be called during mod init.
 *
 * @param <T> the registry value type
 */
public interface FrozenDeferredRegister<T> {

	static <T> FrozenDeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return FrozenLibInitPlatformUtils.REGISTRY.createDeferredRegister(registryKey, namespace);
	}

	static <T> FrozenDeferredRegister<T> create(Registry<T> registry, String namespace) {
		return create(registry.key(), namespace);
	}

	<I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier);

	<I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also);

	<I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier);

	<I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also);

	void register();
}
