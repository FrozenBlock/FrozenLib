/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.block.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A {@link BlockEntityRenderer} that renders a given texture as a billboard, like a particle.
 */
@Environment(EnvType.CLIENT)
public abstract class BillboardBlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState> implements BlockEntityRenderer<T, S> {
	private static final Vector3f Y_AXIS_NEGATIVE = new Vector3f(0F, -1F, 0F);
	private final ModelPart base;

	public BillboardBlockEntityRenderer(Context ctx) {
		ModelPart root = this.getRoot(ctx);
		this.base = root.getChild("base");
	}

	@NotNull
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-8F, -16F, 0F, 16F, 16F, 0.0F),
			PartPose.offsetAndRotation(0F, 0F, 0F, Mth.PI, 0F, 0F)
		);
		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void submit(
		@NotNull S renderState,
		@NotNull PoseStack poseStack,
		@NotNull SubmitNodeCollector submitNodeCollector,
		@NotNull CameraRenderState cameraRenderState
	) {
		poseStack.translate(0.5F, 0F, 0.5F);
		poseStack.pushPose();
		poseStack.mulPose(Mth.rotationAroundAxis(Y_AXIS_NEGATIVE, cameraRenderState.orientation, new Quaternionf()));
		submitNodeCollector.submitModelPart(
			this.base,
			poseStack,
			RenderTypes.entityCutout(this.getTexture(renderState)),
			renderState.lightCoords,
			OverlayTexture.NO_OVERLAY,
			null,
			-1,
			renderState.breakProgress
		);
		poseStack.popPose();
	}

	public abstract ResourceLocation getTexture(S renderState);

	public abstract ModelPart getRoot(Context ctx);
}
