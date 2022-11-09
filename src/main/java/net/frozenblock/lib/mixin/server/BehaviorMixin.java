package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.entities.behavior.impl.FrozenBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Behavior.class)
public class BehaviorMixin<E extends LivingEntity> implements FrozenBehavior {

	@Unique
	private int wilderWild$duration;

	@Inject(method = "tryStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/Behavior;start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;J)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void tryStart(ServerLevel level, E owner, long gameTime, CallbackInfoReturnable<Boolean> cir, int i) {
		this.wilderWild$duration = i;
	}

	@Unique
	@Override
	public int getDuration() {
		return this.wilderWild$duration;
	}
}
