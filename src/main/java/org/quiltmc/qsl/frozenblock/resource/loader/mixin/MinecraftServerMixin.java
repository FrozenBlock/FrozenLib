/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.resource.loader.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Modified to work on Fabric
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract ResourceManager getResourceManager();

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private void onReloadResourcesStart(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload((MinecraftServer) (Object) this,
                this.getResourceManager());
    }

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void onReloadResourcesEnd(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().handleAsync((value, throwable) -> {
            ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload((MinecraftServer) (Object) this,
                    this.getResourceManager(), throwable);
            return value;
        }, (MinecraftServer) (Object) this);
    }
}
