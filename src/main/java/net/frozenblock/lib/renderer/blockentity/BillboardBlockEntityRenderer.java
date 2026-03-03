/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A {@link BlockEntityRenderer} that renders a given texture as a billboard, like a particle.
 */
@Environment(EnvType.CLIENT)
public abstract class BillboardBlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState> implements BlockEntityRenderer<T, S> {
	private static final Vector3f Y_AXIS_NEGATIVE = new Vector3f(0F, -1F, 0F);

	public BillboardBlockEntityRenderer(Context context) {
	}

	@Override
	public void submit(
		S renderState,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		CameraRenderState cameraState
	) {
		poseStack.translate(0.5F, 0F, 0.5F);
		poseStack.pushPose();
		poseStack.mulPose(Mth.rotationAroundAxis(Y_AXIS_NEGATIVE, cameraState.orientation, new Quaternionf()));
		collector.submitModelPart(
			this.base,
			poseStack,
			RenderTypes.entityCutout(this.getSprite(renderState)),
			renderState.lightCoords,
			OverlayTexture.NO_OVERLAY,
			null,
			-1,
			renderState.breakProgress
		);
		poseStack.popPose();
	}

	//CREDIT TO magistermaks ON GITHUB!!
	protected void render(
		PoseStack.Pose pose,
		VertexConsumer vertexConsumer,
		TextureAtlasSprite sprite,
		int lightCoords,
		int overlayCoords,
		int tint
	) {
		final Matrix4f matrix = pose.pose();
		final float u0 = sprite.getU0();
		final float u1 = sprite.getU1();
		final float v0 = sprite.getV0();
		final float v1 = sprite.getV1();

		final Vector3f transformedNormal = pose.transformNormal(0F, 1F, 0F, new Vector3f());
		final float normalX = transformedNormal.x;
		final float normalY = transformedNormal.y;
		final float normalZ = transformedNormal.z;

		vertexConsumer
			.addVertex(matrix, -0.5F, -0.5F, 0F)
			.setColor(tint)
			.setUv(u0, v1)
			.setOverlay(overlayCoords)
			.setLight(lightCoords)
			.setNormal(normalX, normalY, normalZ);
		vertexConsumer
			.addVertex(matrix, 0.5F, -0.5F, 0F)
			.setColor(tint)
			.setUv(u1, v1)
			.setOverlay(overlayCoords)
			.setLight(lightCoords)
			.setNormal(normalX, normalY, normalZ);
		vertexConsumer
			.addVertex(matrix, 0.5F, 0.5F, 0F)
			.setColor(tint)
			.setUv(u1, v0)
			.setOverlay(overlayCoords)
			.setLight(lightCoords)
			.setNormal(normalX, normalY, normalZ);
		vertexConsumer
			.addVertex(matrix, -0.5F, 0.5F, 0F)
			.setColor(tint)
			.setUv(u0, v0)
			.setOverlay(overlayCoords)
			.setLight(lightCoords)
			.setNormal(normalX, normalY, normalZ);
	}

	protected static TextureAtlasSprite getSprite(Identifier texture) {
		return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(texture);
	}

	public abstract TextureAtlas getSprite(S renderState);
}
