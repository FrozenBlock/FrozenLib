package net.frozenblock.lib.block.mixin.client.fire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.impl.fire.LavaParticleFireTypeInterface;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LavaParticle.class)
public class LavaParticleMixin implements LavaParticleFireTypeInterface {

	@Unique
	private FireType frozenLib$fireType = null;

	@Unique
	@Override
	public void frozenLib$setFireType(FireType fireType) {
		this.frozenLib$fireType = fireType;
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		)
	)
	public void frozenLib$useSmokeForFireType(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original) {
		particle = this.frozenLib$fireType != null
			? this.frozenLib$fireType.particleSettings().getLavaParticle(particle)
			: particle;
		original.call(instance, particle, x, y, z, xd, yd, zd);
	}

}
