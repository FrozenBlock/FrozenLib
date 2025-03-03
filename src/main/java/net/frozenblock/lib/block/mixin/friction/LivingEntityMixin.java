package net.frozenblock.lib.block.mixin.friction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.friction.BlockFrictionAPI;
import net.frozenblock.lib.block.api.friction.FrictionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@WrapOperation(
		method = "travel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
		)
	)
	private float frictionApi(Block instance, Operation<Float> original, @Local BlockPos blockPos) {
		FrictionContext frictionContext = new FrictionContext(
			this.level(),
			LivingEntity.class.cast(this),
			this.level().getBlockState(blockPos),
			original.call(instance)
		);
		BlockFrictionAPI.MODIFICATIONS.invoker().modifyFriction(frictionContext);

		return frictionContext.friction;
	}
}
