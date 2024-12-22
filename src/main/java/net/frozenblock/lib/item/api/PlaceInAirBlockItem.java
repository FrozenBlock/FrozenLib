package net.frozenblock.lib.item.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
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

		Vec3 lookAngle = player.getLookAngle();
		Vec3 placementPos = player.getEyePosition().add(
			lookAngle.scale(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE))
		);
		BlockPos pos = BlockPos.containing(placementPos);

		if (level.isInWorldBounds(pos) && level.getWorldBorder().isWithinBounds(pos) && level.getBlockState(pos).canBeReplaced()) {
			Direction reflectedFacingDirection = Direction.getNearest(lookAngle);
			BlockPlaceContext context = new BlockPlaceContext(level, player, hand, itemStack, new BlockHitResult(pos.getCenter(), reflectedFacingDirection, pos, false));
			InteractionResult result = this.useOn(context);
			if (result.consumesAction()) {
				return InteractionResultHolder.sidedSuccess(itemStack, !level.isClientSide());
			}
		}
		return super.use(level, player, hand);
	}
}
