/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
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
import org.jetbrains.annotations.NotNull;
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
	public <T extends Entity> void frozenLib$renderIcon(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		SpottingIconManager iconManager = ((EntitySpottingIconInterface) entity).getSpottingIconManager();
		SpottingIconManager.SpottingIcon icon = iconManager.icon;
		if (icon != null) {
			double dist = Mth.sqrt((float) this.entityRenderDispatcher.distanceToSqr(entity));
			if (dist > icon.startFadeDist() && iconManager.clientHasIconResource) {
				float endDist = icon.endFadeDist() - icon.startFadeDist();
				dist -= icon.startFadeDist();
				int alpha = (int) ((dist > endDist ? 1F : (float) Math.min(1F, dist / endDist)) * 255F);
				float f = entity.getBbHeight() + 1F;
				poseStack.pushPose();
				poseStack.translate(0.0D, f, 0.0D);
				poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
				poseStack.scale(-1, 1, 1);
				PoseStack.Pose pose = poseStack.last();
				VertexConsumer vertexConsumer = buffer.getBuffer(FrozenRenderType.entityTranslucentEmissiveAlwaysRender(((EntitySpottingIconInterface) entity).getSpottingIconManager().icon.texture()));
				frozenLib$vertex(vertexConsumer, pose, packedLight, 0.0F, 0, 0, 1, alpha);
				frozenLib$vertex(vertexConsumer, pose, packedLight, 1.0F, 0, 1, 1, alpha);
				frozenLib$vertex(vertexConsumer, pose, packedLight, 1.0F, 1, 1, 0, alpha);
				frozenLib$vertex(vertexConsumer, pose, packedLight, 0.0F, 1, 0, 0, alpha);
				poseStack.popPose();
			}
		}
	}

	@Unique
	private static void frozenLib$vertex(@NotNull VertexConsumer vertexConsumer, PoseStack.Pose pose, int i, float f, int j, int u, int v, int alpha) {
		vertexConsumer.vertex(pose, f - 0.5F, (float)j - 0.5F, 0.0F)
			.color(255, 255, 255, alpha)
			.uv((float)u, (float)v)
			.overlayCoords(OverlayTexture.NO_OVERLAY)
			.uv2(i)
			.normal(pose, 0.0F, 1.0F, 0.0F)
			.endVertex();
	}
}
