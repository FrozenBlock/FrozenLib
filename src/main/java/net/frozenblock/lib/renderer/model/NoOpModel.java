package net.frozenblock.lib.renderer.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;

@Environment(EnvType.CLIENT)
public class NoOpModel<T extends EntityRenderState> extends EntityModel<T> {

	public NoOpModel(ModelPart root) {
		super(root, RenderTypes::entityCutout);
	}

	public static LayerDefinition createEmptyLayer() {
		return LayerDefinition.create(new MeshDefinition(), 16, 16);
	}
}
