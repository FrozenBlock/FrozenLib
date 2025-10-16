/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.menu.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.menu.api.SplashTextAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SplashManager.class)
public class SplashManagerMixin {

	@ModifyReturnValue(
		method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;",
		at = @At("RETURN")
	)
	public List<String> frozenLib$addSplashFiles(List<String> original, ResourceManager resourceManager, ProfilerFiller profiler) {
		ArrayList<String> splashes = new ArrayList<>(original);
		for (ResourceLocation splashLocation : SplashTextAPI.getSplashFiles()) {
			try {
				BufferedReader bufferedReader = Minecraft.getInstance().getResourceManager().openAsReader(splashLocation);

				List<String> stringList;
				try {
					stringList = bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783).toList();
				} catch (Throwable throwable) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable cantClose) {
						throwable.addSuppressed(cantClose);
                    }

                    throw throwable;
				}

				bufferedReader.close();

				splashes.addAll(stringList);
			} catch (IOException ignored) {
			}
		}

		splashes.addAll(SplashTextAPI.getAdditions());
		for (String removal : SplashTextAPI.getRemovals()) splashes.remove(removal);

		return splashes;
	}

}
