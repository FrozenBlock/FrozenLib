/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.gravity.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

@ApiStatus.Internal
public final class GravityRenderingImpl {
	private GravityRenderingImpl() {}

	private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");

	public static void renderGravityBelts(ClientLevel level, Camera camera, PoseStack poseStack) {
		// not working properly
		if (true) return;
		RenderSystem.defaultBlendFunc();
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		double y = camera.getPosition().y();
		for (GravityAPI.GravityBelt<?> gravityBelt : GravityAPI.getAllBelts(level)) {
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
					RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
					bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					bufferBuilder.vertex(matrix4f3, -k, 0F, -k).uv(0.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, -k).uv(1.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, k).uv(1.0f, 1.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, -k, 0F, k).uv(0.0f, 1.0f).endVertex();
					BufferUploader.drawWithShader(bufferBuilder.end());

					bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					bufferBuilder.vertex(matrix4f3, -k, 0F, k).uv(0.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, k).uv(1.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, -k).uv(1.0f, 1.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, -k, 0F, -k).uv(0.0f, 1.0f).endVertex();
					BufferUploader.drawWithShader(bufferBuilder.end());
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
					bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					bufferBuilder.vertex(matrix4f3, -k, 0F, -k).uv(0.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, -k).uv(1.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, k).uv(1.0f, 1.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, -k, 0F, k).uv(0.0f, 1.0f).endVertex();
					BufferUploader.drawWithShader(bufferBuilder.end());

					bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					bufferBuilder.vertex(matrix4f3, -k, 0F, k).uv(0.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, k).uv(1.0f, 0.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, k, 0F, -k).uv(1.0f, 1.0f).endVertex();
					bufferBuilder.vertex(matrix4f3, -k, 0F, -k).uv(0.0f, 1.0f).endVertex();
					BufferUploader.drawWithShader(bufferBuilder.end());
					poseStack.popPose();
				}
			}
			poseStack.popPose();
		}
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
