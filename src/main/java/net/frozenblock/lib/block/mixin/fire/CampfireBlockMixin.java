package net.frozenblock.lib.block.mixin.fire;

import java.util.Optional;
import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

	@Inject(
		method = "entityInside",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"
		)
	)
	public void frozenLib$setFireType(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo info) {
		final Optional<Holder<FireType>> fireType = FireTypes.getTypeForBlock(entity.registryAccess(), state.getBlock());
		if (fireType.isEmpty()) return;

		FireData.trySet(entity, fireType.get());
	}
}
