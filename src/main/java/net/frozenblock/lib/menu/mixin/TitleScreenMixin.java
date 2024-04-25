/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.menu.mixin;

import java.util.ArrayList;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.math.api.AdvancedMath;
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
				//Panorama isn't valid if one of the six images isn't found; move on to the next ResourceLocation in the list.
				if (Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(namespace, path + "_" + i + ".png")).isEmpty()) {
					FrozenLogUtils.logWarning("Unable to use panorama at " + namespace + ":" + path + ", proper resource pack may not be loaded!", FrozenSharedConstants.UNSTABLE_LOGGING);
					break;
				}
				//Panorama is valid if all six images are found, add to valid panorama list.
				if (i == 5) {
					frozenLib$validPanoramas.add(panLocation);
				}
			}
		}
		if (!frozenLib$validPanoramas.isEmpty()) {
			//Set panorama from a valid list.
			this.panorama = new PanoramaRenderer(new CubeMap(frozenLib$validPanoramas.get(AdvancedMath.random().nextInt(this.frozenLib$validPanoramas.size()))));
			//Clear valid list to save a bit on resources.
			frozenLib$validPanoramas.clear();
		} else {
			//Use the original panorama in case the panorama list is accidentally emptied.
			this.panorama = new PanoramaRenderer(new CubeMap(new ResourceLocation("textures/gui/title/background/panorama")));
		}
	}


}
