/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.item.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
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
    public void changeCooldown(Item item, int additional) {
        if (this.cooldowns.containsKey(item)) {
            CooldownInstance cooldown = this.cooldowns.get(item);
            this.cooldowns.put(item, new CooldownInstance(cooldown.startTime, cooldown.endTime + additional));
            this.onCooldownChanged(item, additional);
        }
    }

	@Unique
	@Override
    public void onCooldownChanged(Item item, int additional) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeId(Registry.ITEM, item);
        byteBuf.writeVarInt(additional);
        ServerPlayNetworking.send(this.player, FrozenMain.COOLDOWN_CHANGE_PACKET, byteBuf);
    }

}
