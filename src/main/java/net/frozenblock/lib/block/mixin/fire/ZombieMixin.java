package net.frozenblock.lib.block.mixin.fire;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {

	@Inject(
		method = "doHurtTarget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$setFireType(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> info) {
		final FireData fireData = Zombie.class.cast(this).getAttached(FireData.ATTACHMENT);
		if (fireData == null || !fireData.type().value().spreadsFromZombie()) return;

		FireData.trySet(target, fireData.type());
	}
}
