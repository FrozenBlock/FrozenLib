/*
 * Copyright 2023 FrozenBlock
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.menu.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
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

	@ModifyReturnValue(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"))
	public List<String> addSplashFiles(List<String> original, ResourceManager resourceManager, ProfilerFiller profiler) {
		for (ResourceLocation splashLocation : SplashTextAPI.getSplashFiles()) {
			try {
				BufferedReader bufferedReader = Minecraft.getInstance().getResourceManager().openAsReader(splashLocation);

				List<String> var4;
				try {
					var4 = bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783).toList();
				} catch (Throwable var7) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
				}

				bufferedReader.close();

				original.addAll(var4);
			} catch (IOException ignored) {

			}
		}
		return original;
	}

}
