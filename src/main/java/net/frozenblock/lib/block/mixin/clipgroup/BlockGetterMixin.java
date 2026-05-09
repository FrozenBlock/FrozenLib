package net.frozenblock.lib.block.mixin.clipgroup;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.api.clipgroup.ClipGroups;
import net.frozenblock.lib.block.impl.clipgroup.ClipGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {

	@Shadow
	BlockState getBlockState(BlockPos pos);

	@Inject(method = "clip", at = @At("HEAD"))
	default void frozenLib$setupClipGroups(ClipContext c, CallbackInfoReturnable<BlockHitResult> info) {
		if (!(c.collisionContext instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() != null)) return;

		final Entity entity = entityCollisionContext.getEntity();
		final BlockState eyeState = this.getBlockState(BlockPos.containing(entity.getEyePosition()));
		for (ClipGroup group : ClipGroups.getAll(entity.registryAccess())) {
			entity.frozenLib$setClipInGroup(group, eyeState != null && group.contains(eyeState));
		}
	}

	@WrapOperation(
		method = "lambda$clip$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/BlockGetter;clipWithInteractionOverride(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/BlockHitResult;"
		)
	)
	default BlockHitResult frozenLib$setShapeToEmptyForClipGroups(
		BlockGetter instance, Vec3 from, Vec3 _to, BlockPos pos, VoxelShape blockShape, BlockState blockState, Operation<BlockHitResult> operation,
		ClipContext context
	) {
		if (context.collisionContext instanceof EntityCollisionContext entityCollisionContext
			&& entityCollisionContext.getEntity() != null
			&& entityCollisionContext.getEntity().frozenLib$wasClipInGroup(blockState)
		) {
			blockShape = Shapes.empty();
		}
		return operation.call(instance, from, _to, pos, blockShape, blockState);
	}

}
