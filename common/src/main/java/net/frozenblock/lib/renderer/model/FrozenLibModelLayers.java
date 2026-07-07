/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.renderer.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;

@Environment(EnvType.CLIENT)
public final class FrozenLibModelLayers {
	public static final ModelLayerLocation NO_MODEL = new ModelLayerLocation(FrozenLibConstants.id("no_model"), "main");

	public static void init() {
		FrozenLibInitPlatformUtils.MODEL_LAYER.registerModelLayer(NO_MODEL, NoOpModel::layer);
	}
}
