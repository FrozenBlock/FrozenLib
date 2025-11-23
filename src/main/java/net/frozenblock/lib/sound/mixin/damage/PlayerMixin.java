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

package net.frozenblock.lib.sound.mixin.damage;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.sound.api.damage.PlayerDamageTypeSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {

	@ModifyReturnValue(method = "getHurtSound", at = @At("RETURN"))
	private SoundEvent frozenLib$playHurtSound(SoundEvent original, DamageSource source) {
		final DamageType type = source.type();
		if (PlayerDamageTypeSounds.containsSource(type)) return PlayerDamageTypeSounds.getDamageSound(type);
		return original;
	}

}
