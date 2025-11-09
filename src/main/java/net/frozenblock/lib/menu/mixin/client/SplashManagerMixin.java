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
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
	public List<Component> frozenLib$addSplashFiles(List<Component> original, ResourceManager resourceManager, ProfilerFiller profiler) {
		ArrayList<Component> splashes = new ArrayList<>(original);

		splashes.addAll(
			SplashTextAPI.getAdditions()
				.stream()
				.map(String::trim)
				.map(SplashManager::literalSplash)
				.toList()
		);

		splashes.removeAll(
			SplashTextAPI.getRemovals()
				.stream()
				.map(String::trim)
				.map(SplashManager::literalSplash)
				.toList()
		);

		for (Identifier splashLocation : SplashTextAPI.getSplashFiles()) {
			try (BufferedReader bufferedReader = resourceManager.openAsReader(splashLocation)) {
				splashes.addAll(
					bufferedReader.lines()
						.map(String::trim)
						.map(SplashManager::literalSplash)
						.toList()
				);
			} catch (IOException ignored) {
			}
		}

		return splashes;
	}

}
