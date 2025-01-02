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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PlaceInAirBlockItem extends BlockItem {

	public PlaceInAirBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	@NotNull
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);

		double blockInteractionRange = player.blockInteractionRange();

		Vec3 lookAngle = player.getLookAngle();
		Vec3 playerPos = player.getEyePosition();
		Vec3 placementPos = playerPos.add(
			lookAngle.scale(blockInteractionRange)
		);

		AABB entityPickBox = player.getBoundingBox().expandTowards(lookAngle.scale(blockInteractionRange)).inflate(1D, 1D, 1D);
		EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
			player,
			playerPos,
			placementPos,
			entityPickBox,
			entityx -> !entityx.isSpectator() && entityx.isPickable(),
			Mth.square(blockInteractionRange)
		);

		if (entityHitResult == null) {
			BlockPos pos = BlockPos.containing(placementPos);

			if (level.isInWorldBounds(pos) && level.getWorldBorder().isWithinBounds(pos) && level.getBlockState(pos).canBeReplaced()) {
				Direction reflectedFacingDirection = Direction.getNearest(lookAngle);
				BlockPlaceContext context = new BlockPlaceContext(player, hand, itemStack, new BlockHitResult(pos.getCenter(), reflectedFacingDirection, pos, false));
				InteractionResult result = this.useOn(context);
				if (result.consumesAction()) {
					return InteractionResultHolder.sidedSuccess(itemStack, !level.isClientSide());
				}
			}
		}
		return super.use(level, player, hand);
	}
}
