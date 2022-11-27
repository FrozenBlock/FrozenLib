/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.mixin.client;

import java.util.ArrayList;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.menu.api.Panoramas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	@Shadow
	@Final
	@Mutable
	private PanoramaRenderer panorama;

	@Unique
	private final ArrayList<ResourceLocation> frozenLib$validPanoramas = new ArrayList<>();

	@Inject(method = "<init>(Z)V", at = @At("TAIL"))
	public void multiplePans(boolean fading, CallbackInfo info) {
		for (ResourceLocation panLocation : Panoramas.getPanoramas()) {
			String namespace = panLocation.getNamespace();
			String path = panLocation.getPath();
			for(int i = 0; i < 6; ++i) {
				//Panorama isn't valid if one of the six images aren't found- move on to next ResourceLocation in the list.
				if (Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(namespace, path + "_" + i + ".png")).isEmpty()) {
					FrozenMain.warn("Unable to use panorama at " + namespace + ":" + path + ", proper resource pack may not be loaded!", true);
					break;
				}
				//Panorama is valid if all six images are found, add to valid panorama list.
				if (i == 5) {
					frozenLib$validPanoramas.add(panLocation);
				}
			}
		}
		if (!frozenLib$validPanoramas.isEmpty()) {
			//Set panorama from valid list.
			this.panorama = new PanoramaRenderer(new CubeMap(frozenLib$validPanoramas.get((int) (Math.random() * frozenLib$validPanoramas.size()))));
			//Clear valid list to save a bit on resources.
			frozenLib$validPanoramas.clear();
		} else {
			//Use original panorama in case panorama list is accidentally emptied.
			this.panorama = new PanoramaRenderer(new CubeMap(new ResourceLocation("textures/gui/title/background/panorama")));
		}
	}


}
