/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.gravity.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class GravityRenderingImpl {
	private GravityRenderingImpl() {}

	private static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
	//TODO: TREETRAIN1 PORT
	// Please see WorldBorderRenderer

	public static void renderGravityBelts(ClientLevel level, Camera camera, PoseStack poseStack) {
		/*
		// not working properly
		if (true) return;
		RenderSystem.defaultBlendFunc();
		double y = camera.getPosition().y();
		for (GravityBelt<?> gravityBelt : GravityAPI.getAllBelts(level)) {
			poseStack.pushPose();
			poseStack.mulPose(Axis.YP.rotationDegrees(-90F));
			if (gravityBelt.renderTop()) {
				float distance = (float) (gravityBelt.maxY() - y);
				float alpha = Mth.lerp(Mth.clamp(Math.abs(distance), 0, 32) / 32F, 1F, 0F);
				if (alpha > 0) {
					RenderSystem.setShaderColor(0.25f, 0.45f, 1.0f, 1.0f);
					poseStack.pushPose();
					poseStack.translate(0, distance, 0);
					//poseStack.mulPose(Axis.XP.rotationDegrees((rotation - xRot) * 360F));
					Matrix4f matrix4f3 = poseStack.last().pose();

					float k = 130;
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
					var firstBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					firstBuffer.addVertex(matrix4f3, -k, 0F, -k).setUv(0.0f, 0.0f);
					firstBuffer.addVertex(matrix4f3, k, 0F, -k).setUv(1.0f, 0.0f);
					firstBuffer.addVertex(matrix4f3, k, 0F, k).setUv(1.0f, 1.0f);
					firstBuffer.addVertex(matrix4f3, -k, 0F, k).setUv(0.0f, 1.0f);
					BufferUploader.drawWithShader(firstBuffer.build());

					var secondBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					secondBuffer.addVertex(matrix4f3, -k, 0F, k).setUv(0.0f, 0.0f);
					secondBuffer.addVertex(matrix4f3, k, 0F, k).setUv(1.0f, 0.0f);
					secondBuffer.addVertex(matrix4f3, k, 0F, -k).setUv(1.0f, 1.0f);
					secondBuffer.addVertex(matrix4f3, -k, 0F, -k).setUv(0.0f, 1.0f);
					BufferUploader.drawWithShader(secondBuffer.build());
					poseStack.popPose();
				}
			}

			if (gravityBelt.renderBottom()) {
				float distance = (float) (gravityBelt.minY() - y);
				float alpha = Mth.lerp(Mth.clamp(Math.abs(distance), 0, 32) / 32F, 1F, 0F);
				if (alpha > 0) {
					RenderSystem.setShaderColor(0.25f, 0.45f, 1.0f, 1.0f);
					poseStack.pushPose();
					poseStack.translate(0, distance, 0);
					//poseStack.mulPose(Axis.XP.rotationDegrees((rotation - xRot) * 360F));
					Matrix4f matrix4f3 = poseStack.last().pose();

					float k = 130;
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
					var firstBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					firstBuffer.addVertex(matrix4f3, -k, 0F, -k).setUv(0.0f, 0.0f);
					firstBuffer.addVertex(matrix4f3, k, 0F, -k).setUv(1.0f, 0.0f);
					firstBuffer.addVertex(matrix4f3, k, 0F, k).setUv(1.0f, 1.0f);
					firstBuffer.addVertex(matrix4f3, -k, 0F, k).setUv(0.0f, 1.0f);
					BufferUploader.drawWithShader(firstBuffer.build());

					var secondBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					secondBuffer.addVertex(matrix4f3, -k, 0F, k).setUv(0.0f, 0.0f);
					secondBuffer.addVertex(matrix4f3, k, 0F, k).setUv(1.0f, 0.0f);
					secondBuffer.addVertex(matrix4f3, k, 0F, -k).setUv(1.0f, 1.0f);
					secondBuffer.addVertex(matrix4f3, -k, 0F, -k).setUv(0.0f, 1.0f);
					BufferUploader.drawWithShader(secondBuffer.build());
					poseStack.popPose();
				}
			}
			poseStack.popPose();
		}
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		 */
	}
}
