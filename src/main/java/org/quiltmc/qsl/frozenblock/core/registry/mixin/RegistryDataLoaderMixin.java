/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import org.quiltmc.qsl.frozenblock.core.registry.impl.DynamicRegistryManagerSetupContextImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Modified to work on Fabric
 */
@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {

	@Inject(
		method = "lambda$load$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/RegistryDataLoader;createContext(Ljava/util/List;Ljava/util/List;)Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;",
			ordinal = 0,
			shift = At.Shift.AFTER
		)
	)
	private static void onDynamicSetup(
		List<RegistryDataLoader.RegistryData<?>> registriesToLoad,
		RegistryDataLoader.LoaderFactory loaderFactory,
		List<HolderLookup.RegistryLookup<?>> contextRegistries,
		Executor executor,
		CallbackInfoReturnable<CompletableFuture<?>> cir,
		@Local(ordinal = 2) List<RegistryDataLoader.RegistryLoadTask<?>> loadTasks
	) {
		RegistryEvents.DYNAMIC_REGISTRY_SETUP.invoker().onDynamicRegistrySetup(
			new DynamicRegistryManagerSetupContextImpl(loadTasks.stream().map(task -> task.registry))
		);
	}

	@Inject(
		method = "lambda$load$2",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
			ordinal = 0
		)
	)
	private static void onDynamicLoaded(
		List<RegistryDataLoader.RegistryLoadTask<?>> loadTasks,
		Map<ResourceKey<?>, Exception> loadingErrors,
		Void ignored,
		CallbackInfoReturnable<RegistryAccess.Frozen> cir
	) {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.invoker().onDynamicRegistryLoaded(
			new DynamicRegistryManagerSetupContextImpl(loadTasks.stream().map(task -> task.registry))
		);
	}
}
