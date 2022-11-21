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

package net.frozenblock.lib.sound.api;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class FrozenClientPacketToServer {

	public static void sendFrozenSoundSyncRequest(int id, ResourceKey<Level> level) {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeVarInt(id);
		byteBuf.writeResourceKey(level);
		ClientPlayNetworking.send(FrozenMain.REQUEST_LOOPING_SOUND_SYNC_PACKET, byteBuf);
	}

	public static void sendFrozenIconSyncRequest(int id, ResourceKey<Level> level) {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeVarInt(id);
		byteBuf.writeResourceKey(level);
		ClientPlayNetworking.send(FrozenMain.REQUEST_SPOTTING_ICON_SYNC_PACKET, byteBuf);
	}

}
