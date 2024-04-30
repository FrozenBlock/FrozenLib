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

package net.frozenblock.lib.item.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ServerItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerItemCooldowns.class)
public class ServerItemCooldownsMixin extends ItemCooldowns implements CooldownInterface {
    @Shadow
	@Final
    private ServerPlayer player;

	@Unique
	@Override
    public void frozenLib$changeCooldown(Item item, int additional) {
		this.cooldowns.computeIfPresent(item, (item1, cooldown) -> {
            this.frozenLib$onCooldownChanged(item, additional);
			return new CooldownInstance(cooldown.startTime, cooldown.endTime + additional);
        });
    }

	@Unique
	@Override
    public void frozenLib$onCooldownChanged(Item item, int additional) {
		ServerPlayNetworking.send(this.player, new CooldownChangePacket(item, additional));
    }

}
