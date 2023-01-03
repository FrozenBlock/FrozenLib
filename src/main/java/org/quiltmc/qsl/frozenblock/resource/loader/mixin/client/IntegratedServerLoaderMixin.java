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

package org.quiltmc.qsl.frozenblock.resource.loader.mixin.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Modified to work on Fabric
 */
@Mixin(WorldOpenFlows.class)
public abstract class IntegratedServerLoaderMixin {
    @Shadow
    private static void safeCloseAccess(LevelStorageSource.LevelStorageAccess storageSession, String worlName) {
        throw new IllegalStateException("Mixin injection failed.");
    }

    @Shadow
    protected abstract void doLoadLevel(Screen parentScreen, String worldName, boolean safeMode, boolean requireBackup);

    @Inject(
            method = "loadWorldDataBlocking",
            at = @At("HEAD")
    )
    private void onStartDataPackLoad(WorldLoader.PackConfig dataPackConfig, WorldLoader.WorldDataSupplier<WorldData> savePropertiesSupplier,
									 WorldLoader.ResultFactory resultFactory,
                                     CallbackInfoReturnable<WorldStem> cir) {
        ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(null, null);
    }

    @Inject(
            method = "loadWorldDataBlocking",
            at = @At("RETURN")
    )
    private void onEndDataPackLoad(WorldLoader.PackConfig dataPackConfig, WorldLoader.WorldDataSupplier<WorldData> savePropertiesSupplier,
								   WorldLoader.ResultFactory resultFactory,
                                   CallbackInfoReturnable<WorldStem> cir) {
        ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(null, cir.getReturnValue().resourceManager(), null);
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
