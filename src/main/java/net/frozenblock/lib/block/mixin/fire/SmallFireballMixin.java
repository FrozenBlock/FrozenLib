package net.frozenblock.lib.block.mixin.fire;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmallFireball.class)
public class SmallFireballMixin {

	@Inject(
		method = "onHitEntity",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$setFireType(EntityHitResult hitResult, CallbackInfo info) {
		final FireData fireData = SmallFireball.class.cast(this).getAttached(FireData.ATTACHMENT);
		if (fireData == null) return;

		FireData.trySet(hitResult.getEntity(), fireData.type());
	}
}
