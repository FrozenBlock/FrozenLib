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

package net.frozenblock.lib.spotting_icons.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.frozenblock.lib.spotting_icons.impl.EntityRenderDispatcherWithIcon;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	@Final
	private RenderBuffers renderBuffers;
	@Shadow
	@Final
	private EntityRenderDispatcher entityRenderDispatcher;
	@Shadow
	@Nullable
	private ClientLevel level;

	@Inject(
		method = "renderLevel",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;",
			shift = At.Shift.AFTER
		)
	)
	public void frozenLib$renderLevel(
		DeltaTracker deltaTracker,
		boolean renderBlockOutline,
		Camera camera,
		GameRenderer gameRenderer,
		LightTexture lightmapTextureManager,
		Matrix4f projectionMatrix,
		Matrix4f matrix4f,
		CallbackInfo info,
		@Local(ordinal = 0) float deltaTime,
		@Local PoseStack poseStack
	) {
		if (this.level != null) {
			MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
			for (Entity entity : this.level.entitiesForRendering()) {
				Vec3 vec3 = camera.getPosition();
				double d = vec3.x();
				double e = vec3.y();
				double f = vec3.z();
				if (entity.tickCount == 0) {
					entity.xOld = entity.getX();
					entity.yOld = entity.getY();
					entity.zOld = entity.getZ();
				}
				this.frozenLib$renderEntityIcon(entity, d, e, f, deltaTime, poseStack, bufferSource);
			}
		}
	}

	@Unique
	private void frozenLib$renderEntityIcon(@NotNull Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
		double d = Mth.lerp(partialTick, entity.xOld, entity.getX());
		double e = Mth.lerp(partialTick, entity.yOld, entity.getY());
		double f = Mth.lerp(partialTick, entity.zOld, entity.getZ());
		float g = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
		((EntityRenderDispatcherWithIcon)this.entityRenderDispatcher).frozenLib$renderIcon(entity, d - camX, e - camY, f - camZ, g, partialTick, poseStack, bufferSource, this.entityRenderDispatcher.getPackedLightCoords(entity, partialTick));
	}

}
