package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.testmod.FrozenTestClient;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {

	@Shadow
	@Final
	private RandomSource random;
	@Shadow @Final private Level level;
	@Shadow @Final private double x;
	@Shadow @Final private double y;
	@Shadow @Final private double z;
	@Shadow @Final private Explosion.BlockInteraction blockInteraction;
	@Shadow @Final private float radius;

	@Inject(method = "finalizeExplosion", at = @At(value = "TAIL"))
	public void finalizeExplosion(boolean spawnParticles, CallbackInfo info) {
		if (this.level.isClientSide) {
			FrozenTestClient.addScreenShakeEasy(new Vec3(this.x, this.y, this.z), 0.2F + (blockInteraction != Explosion.BlockInteraction.NONE ? 0.1F : 0) + radius / 5, radius * 3);
		}
	}

}
