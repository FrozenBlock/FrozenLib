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

package net.frozenblock.lib.item.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.item.api.PlaceInAirBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow
	@Final
	private Minecraft minecraft;
	@Shadow
	@Nullable
	private ClientLevel level;

	@ModifyExpressionValue(
		method = "extractBlockOutline",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/BlockHitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"
		)
	)
	public HitResult.Type frozenLib$useBlockTypeIfPlaceableInAir(
		HitResult.Type original,
		@Local BlockHitResult hitResult,
		@Share("frozenLib$canPlaceInAir") LocalBooleanRef canPlaceInAir
	) {
		canPlaceInAir.set(false);
		if (this.minecraft.player == null || original != HitResult.Type.MISS) return original;

		final BlockPos pos = hitResult.getBlockPos();
		if (PlaceInAirBlockItem.checkIfPlayerCanPlaceBlock(this.minecraft.player, this.level, pos)) {
			canPlaceInAir.set(true);
			return HitResult.Type.BLOCK;
		}

		return original;
	}

	@ModifyExpressionValue(
		method = "extractBlockOutline",
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

	@ModifyExpressionValue(
		method = "extractBlockOutline",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/SharedConstants;DEBUG_SHAPES:Z"
		)
	)
	private boolean frozenLib$fixAirCrash(
		boolean original,
		@Local BlockState state,
		@Share("frozenLib$canPlaceInAir") LocalBooleanRef canPlaceInAir
	) {
		if (state.isAir() && canPlaceInAir.get()) return false;
		return original;
	}

	@ModifyExpressionValue(
		method = "extractBlockOutline",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
		)
	)
	private VoxelShape frozenLib$giveAirFullOutline(
		VoxelShape original,
		@Local BlockState state
	) {
		if (state.isAir()) return Shapes.block();
		return original;
	}

}
