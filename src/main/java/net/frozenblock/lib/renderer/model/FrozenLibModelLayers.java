package net.frozenblock.lib.renderer.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.model.geom.ModelLayerLocation;

@Environment(EnvType.CLIENT)
public final class FrozenLibModelLayers {
	public static final ModelLayerLocation NO_MODEL = new ModelLayerLocation(FrozenLibConstants.id("no_model"), "main");

	public static void init() {
		ModelLayerRegistry.registerModelLayer(NO_MODEL, NoOpModel::createEmptyLayer);
	}
}
