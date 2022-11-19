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

package net.frozenblock.lib.mixin.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.frozenblock.lib.menu.api.Splashes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

	@Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"))
	public void addNewSplashes(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> info) {
		for (ResourceLocation splashLocation : Splashes.getSplashes()) {
			try {
				BufferedReader bufferedReader = Minecraft.getInstance().getResourceManager().openAsReader(splashLocation);

				List<String> var4;
				try {
					var4 = bufferedReader.lines().map(String::trim).filter((splashText) -> splashText.hashCode() != 125780783).collect(Collectors.toList());
				} catch (Throwable var7) {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var6) {
							var7.addSuppressed(var6);
						}
					}

					throw var7;
				}

				bufferedReader.close();

				info.getReturnValue().addAll(var4);
			} catch (IOException ignored) {

			}
		}
	}

}
