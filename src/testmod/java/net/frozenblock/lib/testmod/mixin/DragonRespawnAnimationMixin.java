package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.ScreenShakePackets;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

public class DragonRespawnAnimationMixin {

	@Mixin(targets = "net/minecraft/world/level/dimension/end/DragonRespawnAnimation$2")
	private static class PreparingPillarsMixin {

		@Inject(method = "tick", at = @At("HEAD"))
		private void startShaking(ServerLevel world, EndDragonFight fight, List<EndCrystal> crystals, int i, BlockPos pos, CallbackInfo ci) {
			if (i == 0) {
				ScreenShakePackets.createScreenShakePacket(world, 0.4F, 60, 0, 130, 0, 180);
			}
		}
	}

	@Mixin(targets = "net/minecraft/world/level/dimension/end/DragonRespawnAnimation$4")
	private static class SpawningDragonMixin {

		@Inject(method = "tick", at = @At("TAIL"))
		private void startShaking(ServerLevel world, EndDragonFight fight, List<EndCrystal> crystals, int i, BlockPos pos, CallbackInfo ci) {
			if (i == 0) {
				ScreenShakePackets.createScreenShakePacket(world, 0.7F, 140, 0, 130, 0, 180);
			}
		}
	}
}
