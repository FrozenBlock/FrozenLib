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

package net.frozenblock.lib.platform.client;

import com.mojang.serialization.Lifecycle;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

@ClientOnly
public final class ClientRegistryHelper {

	@PlatformImpl
	public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> MappedRegistry<T> create(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		@Nullable RegistryHelper.RegistryBootstrap<T> bootstrap
	) {
		throw new AssertionError();
	}

	public static <T> MappedRegistry<T> create(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle
	) {
		return create(key, lifecycle, null);
	}
}
