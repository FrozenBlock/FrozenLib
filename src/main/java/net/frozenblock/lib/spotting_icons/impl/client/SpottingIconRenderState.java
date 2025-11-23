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

package net.frozenblock.lib.spotting_icons.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.render.FrozenLibRenderTypes;
import net.frozenblock.lib.spotting_icons.api.SpottingIconManager;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class SpottingIconRenderState {
	public boolean render;
	public Identifier texture;
	public int alpha;
	public float renderOffsetY;

	public void extract(Entity entity, EntityRenderDispatcher entityRenderDispatcher, float partialTick) {
		if (entityRenderDispatcher == null || entityRenderDispatcher.camera == null || entity == null) return;

		final SpottingIconManager iconManager = ((EntitySpottingIconInterface) entity).getSpottingIconManager();
		final SpottingIconManager.SpottingIcon icon = iconManager.icon;
		if (icon == null) return;

		double dist = Math.sqrt(entityRenderDispatcher.camera.position().distanceToSqr(entity.getEyePosition(partialTick)));
		if (dist <= icon.startFadeDist() || !iconManager.clientHasIconResource) return;

		this.texture = icon.texture();
		float endDist = icon.endFadeDist() - icon.startFadeDist();
		dist -= icon.startFadeDist();
		this.alpha = (int) ((dist > endDist ? 1F : (float) Math.min(1F, dist / endDist)) * 255F);
		this.renderOffsetY = entity.getBbHeight() + 1F;
		this.render = true;
	}

	public void submit(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation, SubmitNodeCollector submitNodeCollector) {
		if (!this.render) return;

		poseStack.pushPose();
		poseStack.translate(0F, this.renderOffsetY, 0F);
		poseStack.mulPose(rotation);
		poseStack.scale(-1F, 1F, 1F);

		submitNodeCollector
			.order(0)
			.submitCustomGeometry(
				poseStack,
				FrozenLibRenderTypes.entityTranslucentEmissiveAlwaysRender(this.texture),
				(pose, vertexConsumer) -> renderIcon(pose, vertexConsumer, renderState.lightCoords, this.alpha)
			);
		poseStack.popPose();
	}

	private static void renderIcon(PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int alpha) {
		vertexConsumer
			.addVertex(pose, -0.5F, -0.5F, 0F)
			.setColor(255, 255, 255, alpha)
			.setUv(0, 1)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(packedLight)
			.setNormal(pose, 0F, 1F, 0F);
		vertexConsumer
			.addVertex(pose, 0.5F, -0.5F, 0F)
			.setColor(255, 255, 255, alpha)
			.setUv(1, 1)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(packedLight)
			.setNormal(pose, 0F, 1F, 0F);
		vertexConsumer
			.addVertex(pose, 0.5F, 0.5F, 0F)
			.setColor(255, 255, 255, alpha)
			.setUv(1, 0)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(packedLight)
			.setNormal(pose, 0F, 1F, 0F);
		vertexConsumer
			.addVertex(pose, -0.5F, 0.5F, 0F)
			.setColor(255, 255, 255, alpha)
			.setUv(0, 0)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(packedLight)
			.setNormal(pose, 0F, 1F, 0F);
	}
}
