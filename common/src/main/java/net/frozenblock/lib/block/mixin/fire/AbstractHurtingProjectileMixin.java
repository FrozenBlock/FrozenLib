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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHurtingProjectile.class)
public class AbstractHurtingProjectileMixin {

	@ModifyReturnValue(method = "getTrailParticle", at = @At("RETURN"))
	public ParticleOptions frozenLib$useFireTypeSmoke(ParticleOptions original) {
		if (!original.equals(ParticleTypes.SMOKE)) return original;

		final FireData fireData = FireData.ATTACHMENT.get(AbstractHurtingProjectile.class.cast(this));
		if (fireData == null) return original;

		return fireData.type().value().particleSettings().getSmokeParticle(original);
	}
}
