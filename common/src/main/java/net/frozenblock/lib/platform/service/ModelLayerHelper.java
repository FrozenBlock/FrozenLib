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

package net.frozenblock.lib.platform.service;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.renderer.model.NoOpModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

@Environment(EnvType.CLIENT)
public interface ModelLayerHelper {

	void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> provider);

	default void registerBoatModelLayers(ModelLayerLocation boat, ModelLayerLocation chestBoat, LayerDefinition boatLayer, LayerDefinition chestBoatLayer) {
		this.registerModelLayer(boat, () -> boatLayer);
		this.registerModelLayer(chestBoat, () -> chestBoatLayer);
	}

	default void registerNoOpModelLayer(ModelLayerLocation layer) {
		this.registerModelLayer(layer, NoOpModel::layer);
	}
}
