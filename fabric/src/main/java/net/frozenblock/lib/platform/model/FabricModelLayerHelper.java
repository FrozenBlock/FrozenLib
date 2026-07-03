package net.frozenblock.lib.platform.model;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.frozenblock.lib.platform.service.ModelLayerHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

@Environment(EnvType.CLIENT)
public class FabricModelLayerHelper implements ModelLayerHelper {

	@Override
	public void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> provider) {
		ModelLayerRegistry.registerModelLayer(layer, provider::get);
	}
}
