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

package net.frozenblock.lib.cape.networking;

import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class CapeCustomizePacket implements CustomPacketPayload {
	public static final Type<CapeCustomizePacket> PACKET_TYPE = new Type<>(FrozenSharedConstants.id("cape_packet"));
	public static final StreamCodec<FriendlyByteBuf, CapeCustomizePacket> CODEC = StreamCodec.ofMember(CapeCustomizePacket::write, CapeCustomizePacket::new);

	private final UUID playerUUID;
	private final boolean enabled;
	private ResourceLocation capeTexture = null;

	private CapeCustomizePacket(UUID uuid, boolean enabled) {
		this.playerUUID = uuid;
		this.enabled = enabled;
	}

	private CapeCustomizePacket(UUID uuid, boolean enabled, ResourceLocation capeId) {
		this(uuid, enabled && capeId != null);
		this.capeTexture = capeId;
	}

	public CapeCustomizePacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readUUID(), buf.readBoolean());
		if (this.enabled) {
			this.capeTexture = buf.readResourceLocation();
		}
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeUUID(this.playerUUID);
		buf.writeBoolean(this.enabled);
		if (this.enabled) {
			buf.writeResourceLocation(this.capeTexture);
		}
	}

	public static @NotNull CapeCustomizePacket createDisablePacket(UUID uuid) {
		return new CapeCustomizePacket(uuid, false);
	}

	public static @NotNull CapeCustomizePacket createPacket(UUID uuid, ResourceLocation capeId) {
		return new CapeCustomizePacket(uuid, true, capeId);
	}

	public static void sendCapeToAll(MinecraftServer server, UUID uuid, ResourceLocation capeId) {
		CapeCustomizePacket frozenCapePacket = capeId == null ? CapeCustomizePacket.createDisablePacket(uuid) : CapeCustomizePacket.createPacket(uuid, capeId);
		PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, frozenCapePacket));
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public ResourceLocation getCapeTexture() {
		return this.capeTexture;
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
