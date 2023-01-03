/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.resource.loader.mixin.server;

import net.minecraft.server.Main;
import net.minecraft.server.WorldStem;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Modified to work on Fabric
 */
@Mixin(Main.class)
public class MainMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;blockUntilDone(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;",
                    remap = true
            ),
            remap = false
    )
    private static void onStartReloadResources(String[] strings, CallbackInfo ci) {
        ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null); // First reload
    }

    @ModifyVariable(method = "main", at = @At(value = "STORE"), remap = false)
    private static WorldStem onSuccessfulReloadResources(WorldStem resources) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, resources.resourceManager(), null);
        return resources; // noop
    }

    @ModifyArg(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Throwable;)V"
            ),
            index = 1,
            remap = false
    )
    private static Throwable onFailedReloadResources(Throwable exception) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, null, exception);
        return exception; // noop
    }
}
