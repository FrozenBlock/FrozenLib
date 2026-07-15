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

package net.frozenblock.lib.item.mixin.cooldown;

import net.frozenblock.lib.item.impl.cooldown.SerializableItemCooldowns;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	// TODO: test if this works as intended
	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo info) {
		final ServerPlayer player = ServerPlayer.class.cast(this);
		SerializableItemCooldowns.ATTACHMENT.getOptional(player).ifPresent(cooldowns -> {
			cooldowns.syncWithTarget(player);
			SerializableItemCooldowns.ATTACHMENT.remove(player);
		});
	}
}
