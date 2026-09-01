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

package net.frozenblock.lib.block.mixin.fire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Blaze.class)
public class BlazeMixin {

	@WrapOperation(
		method = "aiStep",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		)
	)
	public void frozenLib$useFireTypeSmoke(Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original) {
		final Blaze blaze = Blaze.class.cast(this);
		final FireData fireData = FireData.ATTACHMENT.get(blaze);
		if (fireData != null) {
			final FireType.ParticleSettings particleSettings = fireData.type().value().particleSettings();
			particle = particleSettings.getLargeSmokeParticle(particle);
		}

		original.call(instance, particle, x, y, z, xd, yd, zd);
	}
}
