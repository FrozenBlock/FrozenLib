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

package net.frozenblock.lib.item.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PlaceInAirBlockItem extends BlockItem {

	public PlaceInAirBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		final ItemStack stack = player.getItemInHand(hand);

		final double blockInteractionRange = player.blockInteractionRange();

		final Vec3 lookAngle = player.getLookAngle();
		final Vec3 playerPos = player.getEyePosition();
		final Vec3 placementPos = playerPos.add(lookAngle.scale(blockInteractionRange));

		final AABB entityPickBox = player.getBoundingBox().expandTowards(lookAngle.scale(blockInteractionRange)).inflate(1D, 1D, 1D);
		final EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
			player,
			playerPos,
			placementPos,
			entityPickBox,
			entityx -> !entityx.isSpectator() && entityx.isPickable(),
			Mth.square(blockInteractionRange)
		);

		place: {
			if (entityHitResult != null) break place;

			final BlockPos pos = BlockPos.containing(placementPos);
			if (!this.checkIfPlayerCanPlaceBlock(player, stack, level, pos)) return InteractionResult.PASS;

			if (!level.isInWorldBounds(pos) || !level.getWorldBorder().isWithinBounds(pos) || !level.getBlockState(pos).canBeReplaced()) break place;

			final Direction reflectedFacingDirection = Direction.getApproximateNearest(lookAngle);
			final BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, new BlockHitResult(pos.getCenter(), reflectedFacingDirection, pos, false));
			final InteractionResult result = this.useOn(context);
			if (result.consumesAction()) return InteractionResult.SUCCESS;
		}

		return super.use(level, player, hand);
	}

	public boolean checkIfPlayerCanPlaceBlock(Player player, ItemStack stack, Level level, BlockPos pos) {
		if (player.isSpectator()) return false;
		if (!player.getAbilities().mayBuild && !stack.canPlaceOnBlockInAdventureMode(new BlockInWorld(level, pos, false))) return false;
		return true;
	}

	public static boolean checkIfPlayerCanPlaceBlock(ItemStack stack, Player player, Level level, BlockPos pos) {
		if (!(stack.getItem() instanceof PlaceInAirBlockItem placeInAirBlockItem)) return false;
		return placeInAirBlockItem.checkIfPlayerCanPlaceBlock(player, stack, level, pos);
	}

	public static boolean checkIfPlayerCanPlaceBlock(Player player, Level level, BlockPos pos) {
		return checkIfPlayerCanPlaceBlock(player.getMainHandItem(), player, level, pos) ||checkIfPlayerCanPlaceBlock(player.getOffhandItem(), player, level, pos);
	}
}
