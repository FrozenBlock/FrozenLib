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

import com.mojang.authlib.GameProfile;
import net.frozenblock.lib.spottingicon.api.SpottingIcon;
import net.frozenblock.lib.spottingicon.api.SpottingIcons;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initWithIcon(Level level, GameProfile gameProfile, CallbackInfo info) {
		SpottingIcons.addIcon(
			Player.class.cast(this),
			SpottingIcon.builder()
				.texture(FrozenTestMain.id("textures/spotting_icons/player.png"))
				.fader(0F, 1F, 0F, 1F)
				.build()
		);
	}
}
