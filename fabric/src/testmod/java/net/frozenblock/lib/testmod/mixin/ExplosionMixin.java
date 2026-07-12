/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.api.ScreenShake;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerExplosion.class)
public class ExplosionMixin {

	@Shadow
	@Final
	private ServerLevel level;
	@Shadow
	@Final
	private Explosion.BlockInteraction blockInteraction;
	@Shadow
	@Final
	private float radius;
	@Shadow
	@Final
	private Vec3 center;

	@Inject(method = "explode", at = @At(value = "TAIL"))
	public void finalizeExplosion(CallbackInfoReturnable<Integer> info) {
		ScreenShakes.add(
			this.level,
			ScreenShake.builder(this.level, this.center)
				.intensity((float) ((0.5F + (blockInteraction != Explosion.BlockInteraction.KEEP ? 0.2F : 0) + radius * 0.1) / 5F))
				.duration((int) ((radius * 5) + 3))
				.maxDistance(this.radius * 2).build()
		);
	}
}
