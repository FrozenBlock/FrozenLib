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

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.menu.api.PanoramaAPI;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CubeMap.class)
public class CubeMapMixin {

	@Shadow
	@Final
	private ResourceLocation[] images = new ResourceLocation[6];

	@Unique
	private boolean frozenLib$canReplacePanorama;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void frozenLib$ensurePanoramaIsMain(ResourceLocation faces, CallbackInfo info) {
		if (faces.equals(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"))) {
			this.frozenLib$canReplacePanorama = true;
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	public void frozenLib$render(Minecraft client, float x, float y, float alpha, CallbackInfo info) {
		if (this.frozenLib$canReplacePanorama) {
			this.frozenLib$canReplacePanorama = false;
			List<ResourceLocation> validPanoramas = new ArrayList<>();
			for (ResourceLocation panLocation : PanoramaAPI.getPanoramas()) {
				String namespace = panLocation.getNamespace();
				String path = panLocation.getPath();
				for (int i = 0; i < 6; ++i) {
					// Panorama isn't valid if one of the six images isn't found; move on to the next ResourceLocation in the list.
					if (Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(namespace, path + "_" + i + ".png")).isEmpty()) {
						FrozenLibLogUtils.logWarning("Unable to use panorama at " + namespace + ":" + path + ", proper resource pack may not be loaded!", FrozenLibConstants.UNSTABLE_LOGGING);
						break;
					}
					// Panorama is valid if all six images are found, add to valid panorama list.
					if (i == 5) {
						validPanoramas.add(panLocation);
					}
				}
			}
			if (!validPanoramas.isEmpty()) {
				// Set panorama from a valid list.
				this.frozenLib$replacePanoramaWith(Util.getRandom(validPanoramas, AdvancedMath.random()));
			}
		}
	}

	@Unique
	private void frozenLib$replacePanoramaWith(ResourceLocation faces) {
		for (int i = 0; i < 6; i++) {
			this.images[i] = faces.withPath(faces.getPath() + "_" + i + ".png");
		}
	}
}
