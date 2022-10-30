package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.ScreenShakePackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.Emerging;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Emerging.class)
public class EmergingMixin<E extends Warden> {

	@Inject(method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;J)V", at = @At("TAIL"))
	private void startShaking(ServerLevel serverLevel, E warden, long l, CallbackInfo ci) {
		ScreenShakePackets.createScreenShakePacket(serverLevel, 0.3F, WardenAi.EMERGE_DURATION - 30, warden.getX(), warden.getY(), warden.getZ(), 30);
	}
}
