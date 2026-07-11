/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.mixin.client.fire;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.client.impl.fire.LavaParticleFireTypeInterface;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
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
				if (original instanceof LavaParticleFireTypeInterface lavaParticle) lavaParticle.frozenLib$setFireType(fireType);
				break;
			}
		}

		return original;
	}

}
