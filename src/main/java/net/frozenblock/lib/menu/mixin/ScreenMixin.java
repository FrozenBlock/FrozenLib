/*
 * Copyright 2024 FrozenBlock
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.menu.api.Panoramas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
public class ScreenMixin {

	@WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/CubeMap;"))
	private static CubeMap multiplePanoramas(ResourceLocation baseImageLocation, Operation<CubeMap> original) {
		List<ResourceLocation> validPanoramas = new ArrayList<>();
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
					validPanoramas.add(panLocation);
				}
			}
		}
		if (!validPanoramas.isEmpty()) {
			//Set panorama from a valid list.
			return original.call(validPanoramas.get(AdvancedMath.random().nextInt(validPanoramas.size())));
		}
		return original.call(baseImageLocation);
	}
}
