/*
 * Copyright 2024 FrozenBlock
 * Copyright 2024 FrozenBlock
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
