/*
 * Copyright (C) 2024-2025 FrozenBlock
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

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public abstract class WardenMixin extends Monster {

	private WardenMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initWithIcon(EntityType<? extends Warden> entityType, Level level, CallbackInfo ci) {
		Warden warden = Warden.class.cast(this);
		((EntitySpottingIconInterface) warden).getSpottingIconManager().setIcon(FrozenTestMain.id("textures/spotting_icons/warden.png"), 8, 12, FrozenLibConstants.id("default"));
	}

	@Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/warden/Warden;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
	private void startShaking(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
		ScreenShakeManager.addScreenShake(level, 0.6F, 8, this.getX(), this.getY(), this.getZ(), 15);
	}
}
