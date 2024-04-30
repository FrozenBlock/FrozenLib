/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.sound.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.sound.api.damagesource.PlayerDamageSourceSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@ModifyReturnValue(method = "getHurtSound", at = @At("RETURN"))
	private SoundEvent playHurtSound(SoundEvent original, DamageSource source) {
		if (PlayerDamageSourceSounds.containsSource(source)) {
			return PlayerDamageSourceSounds.getDamageSound(source);
		}
		return original;
	}

}
