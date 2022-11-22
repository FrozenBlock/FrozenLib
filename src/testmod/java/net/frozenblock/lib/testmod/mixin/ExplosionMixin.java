/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.ScreenShakePackets;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
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
	@Shadow
	@Final
	private Level level;
	@Shadow
	@Final
	private double x;
	@Shadow
	@Final
	private double y;
	@Shadow
	@Final
	private double z;
	@Shadow
	@Final
	private Explosion.BlockInteraction blockInteraction;
	@Shadow
	@Final
	private float radius;

	@Inject(method = "finalizeExplosion", at = @At(value = "TAIL"))
	public void finalizeExplosion(boolean spawnParticles, CallbackInfo info) {
		ScreenShakePackets.createScreenShakePacket(this.level, (float) ((0.5F + (blockInteraction != Explosion.BlockInteraction.NONE ? 0.2F : 0) + radius * 0.1) / 5F), (int) ((radius * 5) + 3), 1, this.x, this.y, this.z, radius * 2);
	}

}
