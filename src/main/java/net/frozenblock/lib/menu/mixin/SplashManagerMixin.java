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

package net.frozenblock.lib.menu.mixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.frozenblock.lib.menu.api.SplashTextAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

	@Shadow
	@Final
	private List<String> splashes;

	@Inject(method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
	private void apply(List<String> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		this.splashes.addAll(SplashTextAPI.getAdditions());

		for (String removal : SplashTextAPI.getRemovals()) {
			this.splashes.remove(removal);
		}
	}

	@Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"))
	public void addSplashFiles(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> info) {
		for (ResourceLocation splashLocation : SplashTextAPI.getSplashFiles()) {
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
