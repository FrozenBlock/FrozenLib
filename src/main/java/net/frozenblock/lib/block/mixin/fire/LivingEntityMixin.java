package net.frozenblock.lib.block.mixin.fire;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 987)
public class LivingEntityMixin {

	@Inject(method = "hurtServer", at = @At("HEAD"))
	public void frozenLib$modifyFireDamage(
		ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> info,
		@Local(argsOnly = true) LocalFloatRef damageRef
	) {
		if (!source.is(DamageTypes.ON_FIRE)) return;

		final FireData fireData = LivingEntity.class.cast(this).getAttached(FireData.ATTACHMENT);
		final float newDamage = damage == 1F && fireData != null
			? fireData.type().value().damage()
			: damage;
		damageRef.set(newDamage);
	}
}
