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

package org.quiltmc.qsl.frozenblock.resource.loader.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Modified to work on Fabric
 */
@Mixin(WorldOpenFlows.class)
public abstract class IntegratedServerLoaderMixin {

    @Inject(
            method = "loadWorldDataBlocking",
            at = @At("HEAD")
    )
    private <D, R> void onStartDataPackLoad(WorldLoader.PackConfig dataPackConfig, WorldLoader.WorldDataSupplier<D> savePropertiesSupplier,
									 WorldLoader.ResultFactory<D, R> resultFactory,
                                     CallbackInfoReturnable<R> cir) {
        ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
    }

    @ModifyReturnValue(
            method = "loadWorldDataBlocking",
            at = @At("RETURN")
    )
    private <D, R> R onEndDataPackLoad(R original, WorldLoader.PackConfig dataPackConfig, WorldLoader.WorldDataSupplier<D> savePropertiesSupplier,
								   WorldLoader.ResultFactory<D, R> resultFactory) {
		if (original instanceof WorldStem worldStem) {
			ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, worldStem.resourceManager(), null);
		}
		return original;
    }

    @ModifyArg(
            method = {"createFreshLevel", "doLoadLevel(Lnet/minecraft/client/gui/screens/Screen;Ljava/lang/String;ZZ)V"},
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false),
            index = 1
    )
    private Throwable onFailedDataPackLoad(Throwable throwable) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, null, throwable);
        return throwable; // noop
    }
}
