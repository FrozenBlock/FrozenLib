package net.frozenblock.lib.block.mixin.fire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(
		method = "lavaIgnite",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$lavaIgnite(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
		if (FireData.hasPermanentFireData(entity)) return;
		entity.removeAttached(FireData.ATTACHMENT);
	}

	@Inject(
		method = "thunderHit",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$thunderHit(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
		if (FireData.hasPermanentFireData(entity)) return;
		entity.removeAttached(FireData.ATTACHMENT);
	}

	@WrapOperation(
		method = "setSharedFlagOnFire",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;setSharedFlag(IZ)V"
		)
	)
	public void frozenLib$setSharedFlagOnFire(Entity instance, int flag, boolean value, Operation<Void> original) {
		if (!instance.level().isClientSide() && !value && !FireData.hasPermanentFireData(instance)) instance.removeAttached(FireData.ATTACHMENT);
		original.call(instance, flag, value);
	}

	@Inject(method = "clearFire", at = @At("HEAD"))
	public void frozenLib$clearFire(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
		if (entity.level().isClientSide() || FireData.hasPermanentFireData(entity)) return;
		entity.removeAttached(FireData.ATTACHMENT);
	}

}
