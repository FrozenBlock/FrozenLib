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

package net.frozenblock.lib.spotting_icons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.frozenblock.lib.entity.api.rendering.FrozenRenderType;
import net.frozenblock.lib.spotting_icons.api.SpottingIconManager;
import net.frozenblock.lib.spotting_icons.impl.EntityRendererWithIcon;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> implements EntityRendererWithIcon {

	@Shadow
	@Final
	protected EntityRenderDispatcher entityRenderDispatcher;

	@Unique
	@Override
	public <T extends Entity> void frozenLib$renderIcon(T entity, float entityYaw, float partialTick, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		SpottingIconManager iconManager = ((EntitySpottingIconInterface) entity).getSpottingIconManager();
		SpottingIconManager.SpottingIcon icon = iconManager.icon;
		if (icon != null) {
			double dist = Mth.sqrt((float) this.entityRenderDispatcher.distanceToSqr(entity));
			if (dist > icon.startFadeDist() && iconManager.clientHasIconResource) {
				float endDist = icon.endFadeDist() - icon.startFadeDist();
				dist -= icon.startFadeDist();
				float alpha = dist > endDist ? 1F : (float) Math.min(1F, dist / endDist);
				float f = entity.getBbHeight() + 1F;
				matrixStack.pushPose();
				matrixStack.translate(0.0D, f, 0.0D);
				matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
				matrixStack.scale(-1, 1, 1);
				Matrix4f matrix4f = matrixStack.last().pose();
				Matrix3f matrix3f = matrixStack.last().normal();
				int overlay = OverlayTexture.pack(OverlayTexture.u(0F), OverlayTexture.v(false));
				VertexConsumer vertexConsumer = buffer.getBuffer(FrozenRenderType.entityTranslucentEmissiveAlwaysRender(((EntitySpottingIconInterface) entity).getSpottingIconManager().icon.texture()));
				vertexConsumer
						.vertex(matrix4f, -0.5F, -0.5F, 0.0F)
						.color(1, 1, 1, alpha)
						.uv(0, 1)
						.overlayCoords(overlay)
						.uv2(packedLight)
						.normal(matrix3f, 0.0F, 1.0F, 0.0F)
						.endVertex();
				vertexConsumer
						.vertex(matrix4f, 0.5F, -0.5F, 0.0F)
						.color(1, 1, 1, alpha)
						.uv(1, 1)
						.overlayCoords(overlay)
						.uv2(packedLight)
						.normal(matrix3f, 0.0F, 1.0F, 0.0F)
						.endVertex();
				vertexConsumer
						.vertex(matrix4f, 0.5F, 0.5F, 0.0F)
						.color(1, 1, 1, alpha)
						.uv(1, 0)
						.overlayCoords(overlay)
						.uv2(packedLight)
						.normal(matrix3f, 0.0F, 1.0F, 0.0F)
						.endVertex();
				vertexConsumer
						.vertex(matrix4f, -0.5F, 0.5F, 0.0F)
						.color(1, 1, 1, alpha)
						.uv(0, 0)
						.overlayCoords(overlay)
						.uv2(packedLight)
						.normal(matrix3f, 0.0F, 1.0F, 0.0F)
						.endVertex();

				matrixStack.popPose();
			}
		}
	}
}
