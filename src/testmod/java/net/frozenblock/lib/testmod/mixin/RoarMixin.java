package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.ScreenShakePackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.Roar;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Roar.class)
public class RoarMixin {

	@Shadow
	@Final
	private static int TICKS_BEFORE_PLAYING_ROAR_SOUND;

	@Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/warden/Warden;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.AFTER))
	private void startShaking(ServerLevel serverLevel, Warden warden, long l, CallbackInfo ci) {
		ScreenShakePackets.createScreenShakePacket(serverLevel, 0.5F, WardenAi.ROAR_DURATION - TICKS_BEFORE_PLAYING_ROAR_SOUND, warden.getX(), warden.getY(), warden.getZ(), 23);
	}
}
