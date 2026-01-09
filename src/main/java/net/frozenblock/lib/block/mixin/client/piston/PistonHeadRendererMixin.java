/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.client.piston;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.impl.state.MovingBlockRenderStateImpl;
import net.frozenblock.lib.block.impl.PushableBlockEntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PistonHeadRenderer.class)
public class PistonHeadRendererMixin {

	@ModifyExpressionValue(
		method = "extractRenderState(Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/PistonHeadRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/blockentity/PistonHeadRenderer;createMovingBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/Level;)Lnet/minecraft/client/renderer/block/MovingBlockRenderState;",
			ordinal = 3
		)
	)
	public MovingBlockRenderState frozenLib$extractMovingBlockEntity(
		MovingBlockRenderState original,
		PistonMovingBlockEntity movingBlock, PistonHeadRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
	) {
		if (!(original instanceof MovingBlockRenderStateImpl movingRenderStateImpl)) return original;

		try {
			final BlockEntity fakeBlockEntity = PushableBlockEntityUtil.getFakeBlockEntity(movingBlock);
			if (fakeBlockEntity == null) return original;

			final BlockEntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
			final BlockEntityRenderState fakeRenderState = renderDispatcher.tryExtractRenderState(fakeBlockEntity, partialTick, crumblingOverlay);
			movingRenderStateImpl.frozenLib$setBlockEntityRenderState(fakeRenderState);
		} catch (Throwable ignored) {}

		return original;
	}

	@Inject(
		method = "submit(Lnet/minecraft/client/renderer/blockentity/state/PistonHeadRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitMovingBlock(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/block/MovingBlockRenderState;)V",
			ordinal = 0
		)
	)
	public void frozenLib$SubmitMovingBlockEntity(
		PistonHeadRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo info
	) {
		if (!(renderState.block instanceof MovingBlockRenderStateImpl movingRenderStateImpl)) return;

		final BlockEntityRenderState fakeRenderState = movingRenderStateImpl.frozenLib$getBlockEntityRenderState();
		if (fakeRenderState == null) return;

		try {
			final BlockEntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
			poseStack.pushPose();
			renderDispatcher.submit(fakeRenderState, poseStack, collector, cameraState);
			poseStack.popPose();
		} catch (Throwable ignored) {}
	}

}
