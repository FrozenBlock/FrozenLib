/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.resource.loader.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @ModifyReturnValue(method = "reloadResources", at = @At("RETURN"))
    private CompletableFuture<Void> onReloadResourcesEnd(CompletableFuture<Void> original) {
        original.handleAsync((value, throwable) -> {
            ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload((MinecraftServer) (Object) this,
                    this.getResourceManager(), throwable);
            return value;
        }, (MinecraftServer) (Object) this);
		return original;
	}
}
