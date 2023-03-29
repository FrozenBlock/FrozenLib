/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.Roar;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Roar.class)
public class RoarMixin {

	@Shadow
	@Final
	private static int TICKS_BEFORE_PLAYING_ROAR_SOUND;

	@Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/warden/Warden;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.AFTER))
	private void startShaking(ServerLevel serverLevel, Warden warden, long l, CallbackInfo ci) {
		ScreenShakeManager.addEntityScreenShake(warden, 0.3F, WardenAi.ROAR_DURATION - TICKS_BEFORE_PLAYING_ROAR_SOUND, 19);
	}
}
