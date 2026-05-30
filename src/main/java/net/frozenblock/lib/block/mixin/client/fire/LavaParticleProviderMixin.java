package net.frozenblock.lib.block.mixin.client.fire;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LavaParticle.Provider.class)
public class LavaParticleProviderMixin {

	@ModifyReturnValue(
		method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At("RETURN")
	)
	public Particle frozenLib$setFireTypeIfApplicable(
		Particle original,
		@Local(argsOnly = true) SimpleParticleType options,
		@Local(argsOnly = true) ClientLevel level
	) {
		for (FireType fireType : level.registryAccess().lookupOrThrow(FrozenLibRegistries.FIRE_TYPE)) {
			final FireType.ParticleSettings particleSettings = fireType.particleSettings();
			if (!particleSettings.lavaEnabled()) continue;
			if (particleSettings.lavaParticle().isPresent() && particleSettings.lavaParticle().get().equals(options)) {
				if (original instanceof LavaParticle lavaParticle) lavaParticle.frozenLib$setFireType(fireType);
				break;
			}
		}

		return original;
	}

}
