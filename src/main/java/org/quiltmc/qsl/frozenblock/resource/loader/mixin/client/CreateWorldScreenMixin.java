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

package org.quiltmc.qsl.frozenblock.resource.loader.mixin.client;

import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldCallback;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.DataPackReloadCookie;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContextMapper;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Modified to work on Fabric
 */
@Environment(EnvType.CLIENT)
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

	@Inject(
		method = "openCreateWorldScreen",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/WorldLoader;load(Lnet/minecraft/server/WorldLoader$InitConfig;Lnet/minecraft/server/WorldLoader$WorldDataSupplier;Lnet/minecraft/server/WorldLoader$ResultFactory;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
		)
	)
	private static void onDataPackLoadStart(
		Minecraft minecraft,
		Runnable runnable,
		Function<WorldLoader.DataLoadContext, WorldGenSettings> function,
		WorldCreationContextMapper worldCreationContextMapper,
		ResourceKey<WorldPreset> resourceKey,
		CreateWorldCallback createWorldCallback,
		CallbackInfo info
	) {
		ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
	}

	@Inject(
		method = "lambda$openCreateWorldScreen$1",
		at = @At("HEAD")
	)
	private static void onCreateDataPackLoadEnd(
		WorldCreationContextMapper worldCreationContextMapper,
		CloseableResourceManager resourceManager,
		ReloadableServerResources resources,
		LayeredRegistryAccess<?> layeredRegistryAccess,
		DataPackReloadCookie dataPackReloadCookie,
		CallbackInfoReturnable<WorldCreationContext> info
	) {
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resourceManager, null);
	}

	@Inject(
		method = "lambda$applyNewPackConfig$3",
		at = @At("HEAD")
	)
	private static void onCreateDataPackLoadEnd(
		CloseableResourceManager resourceManager,
		ReloadableServerResources managers,
		LayeredRegistryAccess<?> registries,
		DataPackReloadCookie cookie,
		CallbackInfoReturnable<WorldCreationContext> cir
	) {
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resourceManager, null);
	}

	@Inject(
		method = "applyNewPackConfig",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/WorldLoader;load(Lnet/minecraft/server/WorldLoader$InitConfig;Lnet/minecraft/server/WorldLoader$WorldDataSupplier;Lnet/minecraft/server/WorldLoader$ResultFactory;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
		)
	)
	private void onDataPackLoadStart(
		PackRepository packRepository,
		WorldDataConfiguration worldDataConfiguration,
		Consumer<WorldDataConfiguration> consumer,
		CallbackInfo info
	) {
		ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
	}

	@Inject(
		method = "lambda$applyNewPackConfig$5",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Throwable;)V",
			shift = At.Shift.AFTER,
			remap = false
		)
	)
	private void onFailDataPackLoading(
		Consumer<WorldDataConfiguration> consumer,
		Void unused,
		Throwable throwable,
		CallbackInfoReturnable<Object> info
	) {
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, null, throwable);
	}
}
