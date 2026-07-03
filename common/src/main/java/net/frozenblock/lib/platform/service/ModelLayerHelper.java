package net.frozenblock.lib.platform.service;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

@Environment(EnvType.CLIENT)
public interface ModelLayerHelper {

	void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> provider);
}
