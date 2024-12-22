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

package net.frozenblock.lib.item.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.item.api.PlaceInAirBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyExpressionValue(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"
		)
	)
	public HitResult.Type frozenLib$useBlockTypeIfPlaceableInAir(
		HitResult.Type original,
		@Share("frozenLib$canPlaceInAir") LocalBooleanRef canPlaceInAir
	) {
		if (this.minecraft.player != null && original == HitResult.Type.MISS) {
			if (this.minecraft.player.getMainHandItem().getItem() instanceof PlaceInAirBlockItem || this.minecraft.player.getOffhandItem().getItem() instanceof PlaceInAirBlockItem) {
				canPlaceInAir.set(true);
				return HitResult.Type.BLOCK;
			}
		}

		canPlaceInAir.set(false);
		return original;
	}

	@ModifyExpressionValue(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"
		)
	)
	public boolean frozenLib$overrideAirCheck(
		boolean original,
		@Share("frozenLib$canPlaceInAir") LocalBooleanRef canPlaceInAir
	) {
		return original && !canPlaceInAir.get();
	}

	@WrapOperation(
		method = "renderHitOutline",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"
		)
	)
	private void frozenLib$renderOutlineForAir(
		PoseStack poseStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j, Operation<Void> original,
		@Local(argsOnly = true) BlockState blockState
	) {
		if (blockState.isAir()) voxelShape = Shapes.block();

		original.call(poseStack, vertexConsumer, voxelShape, d, e, f, g, h, i, j);
	}

}
