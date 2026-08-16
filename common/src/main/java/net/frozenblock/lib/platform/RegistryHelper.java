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

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public final class RegistryHelper {

	@PlatformImpl
	public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.Items createDeferredItemsRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.Blocks createDeferredBlocksRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.DataComponents createDeferredDataComponentsRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.Entities createDeferredEntitiesRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.SoundEvents createDeferredSoundEventsRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.ParticleTypes createDeferredParticleTypesRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.MemoryModuleTypes createDeferredMemoryModuleTypesRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static DeferredRegister.Activities createDeferredActivitiesRegister(String namespace) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> MappedRegistry<T> createSimpleRegistry(
		ResourceKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		boolean synced,
		@Nullable RegistryBootstrap<T> bootstrap
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void registerDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void registerSyncedDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec, Codec<T> networkCodec) {
		throw new AssertionError();
	}

	@FunctionalInterface
	public interface RegistryBootstrap<T> {
		void run(Registry<T> registry);
	}
}
