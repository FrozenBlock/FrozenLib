package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.ScreenShakePackets;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends Raider {

	private RavagerMixin(EntityType<? extends Raider> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Ravager;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
	private void startShaking(CallbackInfo ci) {
		ScreenShakePackets.createScreenShakePacket(this.level, 0.5F, 17, this.getX(), this.getY(), this.getZ(), 23);
	}
}
