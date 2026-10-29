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

import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;

@ClientOnly
@UtilityClass
public final class ModelLayerRegistry {
	private static final LayerDefinition BOAT_MODEL_LAYER = BoatModel.createBoatModel();
	private static final LayerDefinition CHEST_BOAT_MODEL_LAYER = BoatModel.createChestBoatModel();

	@PlatformImpl
	public static void register(ModelLayerLocation layer, Supplier<LayerDefinition> provider) {
		throw new AssertionError();
	}

	public static void registerArmorSet(ArmorModelSet<ModelLayerLocation> armorModelSet, ArmorModelSet<LayerDefinition> armor) {
		register(armorModelSet.head(), armor::head);
		register(armorModelSet.chest(), armor::chest);
		register(armorModelSet.legs(), armor::legs);
		register(armorModelSet.feet(), armor::feet);
	}

	public static void registerHumanoidArmorSet(ArmorModelSet<ModelLayerLocation> armorModelSet) {
		final ArmorModelSet<LayerDefinition> humanoidArmor = HumanoidModel.createArmorMeshSet(LayerDefinitions.INNER_ARMOR_DEFORMATION, LayerDefinitions.OUTER_ARMOR_DEFORMATION)
			.map(mesh -> LayerDefinition.create(mesh, 64, 32));

		registerArmorSet(armorModelSet, humanoidArmor);
	}

	public static void registerBabyHumanoidArmorSet(ArmorModelSet<ModelLayerLocation> armorModelSet) {
		final ArmorModelSet<LayerDefinition> humanoidBabyArmor = HumanoidModel.createBabyArmorMeshSet(
			LayerDefinitions.BABY_INNER_ARMOR_DEFORMATION, LayerDefinitions.BABY_OUTER_ARMOR_DEFORMATION, PartPose.ZERO
		).map(mesh -> LayerDefinition.create(mesh, 64, 64));

		registerArmorSet(armorModelSet, humanoidBabyArmor);
	}

	public static void registerBoats(ModelLayerLocation boat, ModelLayerLocation chestBoat) {
		register(boat, () -> BOAT_MODEL_LAYER);
		register(chestBoat, () -> CHEST_BOAT_MODEL_LAYER);
	}
}
