/*
 * Copyright 2023 QuiltMC
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

package org.quiltmc.qsl.frozenblock.resource.loader.mixin.client;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.quiltmc.qsl.frozenblock.resource.loader.impl.DataPackLoadingContext;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Modified to work on Fabric
 */
@Environment(EnvType.CLIENT)
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private static WorldLoader.InitConfig createDefaultLoadConfig(PackRepository resourcePackManager, DataPackConfig dataPackConfig) {
        throw new IllegalStateException("Mixin injection failed.");
    }

    /*@Redirect(
            method = "openFresh",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/WorldLoader;load(Lnet/minecraft/server/WorldLoader$InitConfig;Lnet/minecraft/server/WorldLoader$WorldDataSupplier;Lnet/minecraft/server/WorldLoader$ResultFactory;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private static <D> CompletableFuture<WorldCreationContext> loadDynamicRegistry(WorldLoader.InitConfig initConfig,
                                                                                   WorldLoader.WorldDataSupplier<D> loadContextSupplier, WorldLoader.ResultFactory<D, WorldCreationContext> applierFactory,
                                                                                   Executor prepareExecutor, Executor applyExecutor) {
        return quilt$applyDefaultDataPacks(() -> {
            ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
            return WorldLoader.load(initConfig, (resourceManager, dataPackSettings) -> {
                RegistryAccess.Writable registryManager = RegistryAccess.builtinCopy();
                // Force-loads the dynamic registry from data-packs as some mods may define dynamic game objects via data-driven capabilities.
                RegistryOps.createAndLoad(JsonOps.INSTANCE, registryManager, resourceManager);
                RegistryAccess.Frozen frozen = registryManager.freeze();
                WorldGenSettings generatorOptions = WorldPresets.createNormalWorldFromPreset(frozen);
                return Pair.of(generatorOptions, frozen);
            }, (resourceManager, serverReloadableResources, frozen, generatorOptions) -> {
                ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resourceManager, null);
                resourceManager.close();
                return new WorldCreationContext(generatorOptions, Lifecycle.stable(), frozen, serverReloadableResources);
            }, Util.backgroundExecutor(), Minecraft.getInstance());
        });
    }*/

    @Inject(
            method = "tryApplyNewDataPacks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/WorldLoader;load(Lnet/minecraft/server/WorldLoader$InitConfig;Lnet/minecraft/server/WorldLoader$WorldDataSupplier;Lnet/minecraft/server/WorldLoader$ResultFactory;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private void onDataPackLoadStart(PackRepository repository, CallbackInfo ci) {
        ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
    }

    @Inject(
            method = "openFresh",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/WorldLoader;load(Lnet/minecraft/server/WorldLoader$InitConfig;Lnet/minecraft/server/WorldLoader$WorldDataSupplier;Lnet/minecraft/server/WorldLoader$ResultFactory;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private static void onDataPackLoadStart(Minecraft client, Screen parent, CallbackInfo ci) {
        ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
    }

    // Lambda method in CreateWorldScreen#applyDataPacks, at C_kjxfcecs#method_42098.
    // Inject before closing the resource manager.
    @Inject(
            method = {"method_41850", "m_paskjwcu"},
            at = @At("HEAD"),
            require = 1,
            remap = false // Very bad, someone please fix the Mixin annotation processor already.
    )
    private static void onDataPackLoadEnd(CloseableResourceManager resourceManager,
                                          ReloadableServerResources serverReloadableResources,
                                          RegistryAccess.Frozen frozen, Pair pair,
                                          CallbackInfoReturnable<WorldCreationContext> cir) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resourceManager, null);
    }

    // Lambda method in CreateWorldScreen#create, at C_kjxfcecs#method_42098.
    // Inject before closing the resource manager.
    @Inject(
            method = {"method_41851", "m_tlckpqyc"},
            at = @At("HEAD"),
            require = 1,
            remap = false // Very bad, someone please fix the Mixin annotation processor already.
    )
    private static void onCreateDataPackLoadEnd(CloseableResourceManager resourceManager,
                                                ReloadableServerResources serverReloadableResources,
                                                RegistryAccess.Frozen frozen, WorldGenSettings generatorOptions,
                                                CallbackInfoReturnable<WorldCreationContext> cir) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resourceManager, null);
    }

    // Lambda method in CreateWorldScreen#applyDataPacks, at CompletableFuture#handle.
    // Take Void and Throwable parameters.
    @Inject(
            method = {"method_37089", "m_kltndaqc"},
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Throwable;)V",
                    shift = At.Shift.AFTER,
                    remap = false
            ),
            require = 1,
            remap = false
    )
    private void onFailDataPackLoading(Void unused, Throwable throwable, CallbackInfoReturnable<Object> cir) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, null, throwable);
    }

    @Unique
    private static CompletableFuture<WorldCreationContext> frozenLib_quilt$applyDefaultDataPacks(Supplier<CompletableFuture<WorldCreationContext>> base) {
        var client = Minecraft.getInstance();
        client.tell(() -> client.setScreen(new GenericDirtMessageScreen(Component.translatable("dataPack.validation.working"))));

        WorldLoader.InitConfig initConfig = createDefaultLoadConfig(new PackRepository(PackType.SERVER_DATA, new ServerPacksSource()),
                createDefaultDataPackSettings(DataPackConfig.DEFAULT));
        return WorldLoader.load(
                initConfig,
                (resourceManager, dataPackSettings) -> {
                    var dataPackLoadingContext = new DataPackLoadingContext(RegistryAccess.builtinCopy(), resourceManager);
                    DataResult<WorldGenSettings> result = dataPackLoadingContext.loadDefaultGeneratorOptions(dataPackLoadingContext.loadRegistries());

                    RegistryAccess.Frozen frozenRegistryManager = dataPackLoadingContext.registryManager().freeze();
                    Lifecycle lifecycle = result.lifecycle().add(frozenRegistryManager.allElementsLifecycle());
                    WorldGenSettings generatorOptions = result.getOrThrow(
                            false, Util.prefix("Error parsing world-gen settings after loading data-packs: ", LOGGER::error)
                    );

                    if (frozenRegistryManager.registryOrThrow(Registry.WORLD_PRESET_REGISTRY).size() == 0) {
                        throw new IllegalStateException("Needs at least one world preset to continue");
                    } else if (frozenRegistryManager.registryOrThrow(Registry.BIOME_REGISTRY).size() == 0) {
                        throw new IllegalStateException("Needs at least one biome to continue");
                    } else {
                        return Pair.of(Pair.of(generatorOptions, lifecycle), frozenRegistryManager);
                    }
                },
                (resourceManager, serverReloadableResources, registryManager, pair) -> {
                    ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resourceManager, null);
                    resourceManager.close();
                    return new WorldCreationContext(pair.getFirst(), pair.getSecond(), registryManager, serverReloadableResources);
                },
                Util.backgroundExecutor(),
                client
        ).exceptionallyCompose(error -> {
            LOGGER.warn("Failed to validate default data-pack.", error);
            ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, null, error);
            return base.get();
        });
    }

    private static DataPackConfig createDefaultDataPackSettings(DataPackConfig source) {
        var moddedResourcePacks = new ArrayList<Pack>();

        var enabled = new ArrayList<>(source.getEnabled());
        var disabled = new ArrayList<>(source.getDisabled());

        // This ensure that any built-in registered data packs by mods which needs to be enabled by default are
        // as the data pack screen automatically put any data pack as disabled except the Default data pack.
        for (var profile : moddedResourcePacks) {
            PackResources pack = profile.open();

            enabled.add(profile.getId());
        }

        return new DataPackConfig(enabled, disabled);
    }
}
