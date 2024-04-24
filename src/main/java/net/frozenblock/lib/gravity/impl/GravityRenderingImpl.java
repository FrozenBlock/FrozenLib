/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
import net.frozenblock.lib.gravity.api.GravityBelt;
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
