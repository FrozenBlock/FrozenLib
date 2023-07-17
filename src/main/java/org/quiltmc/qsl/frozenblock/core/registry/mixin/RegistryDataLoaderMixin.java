/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import org.quiltmc.qsl.frozenblock.core.registry.impl.DynamicRegistryManagerSetupContextImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Modified to work on Fabric
 */
@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {

	@Inject(
			method = "load",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
					ordinal = 0,
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void onBeforeLoad(ResourceManager resourceManager, RegistryAccess registryManager, List<RegistryDataLoader.RegistryData<?>> decodingData,
									 CallbackInfoReturnable<RegistryAccess.Frozen> cir,
									 Map<?, ?> map,
									 List<Pair<WritableRegistry<?>, ?>> registries) {
		RegistryEvents.DYNAMIC_REGISTRY_SETUP.invoker().onDynamicRegistrySetup(
				new DynamicRegistryManagerSetupContextImpl(resourceManager, registries.stream().map(Pair::getFirst))
		);
	}

	@Inject(
			method = "load",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
					ordinal = 0,
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void onAfterLoad(ResourceManager resourceManager, RegistryAccess registryManager, List<RegistryDataLoader.RegistryData<?>> decodingData,
									CallbackInfoReturnable<RegistryAccess.Frozen> cir) {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.invoker().onDynamicRegistryLoaded(registryManager);
	}
}
