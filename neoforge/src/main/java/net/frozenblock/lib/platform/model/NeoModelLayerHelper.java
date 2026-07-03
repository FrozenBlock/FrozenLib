package net.frozenblock.lib.platform.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.platform.service.ModelLayerHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Environment(EnvType.CLIENT)
public class NeoModelLayerHelper implements ModelLayerHelper {
	private record Entry(ModelLayerLocation layer, Supplier<LayerDefinition> provider) {}

	private static final List<Entry> LAYERS = new ArrayList<>();

	@Override
	public void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> provider) {
		LAYERS.add(new Entry(layer, provider));
	}

	public static void flush(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (Entry entry : LAYERS) {
			event.registerLayerDefinition(entry.layer(), entry.provider());
		}
	}
}
