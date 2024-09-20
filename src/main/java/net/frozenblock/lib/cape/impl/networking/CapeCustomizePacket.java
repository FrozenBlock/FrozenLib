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

package net.frozenblock.lib.cape.impl.networking;

import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CapeCustomizePacket implements FabricPacket {
	private static final ResourceLocation DUMMY = FrozenSharedConstants.id("dummy");
	public static final PacketType<CapeCustomizePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("cape_packet"), CapeCustomizePacket::new
	);

	private final UUID playerUUID;
	private final boolean enabled;
	private ResourceLocation capeId = null;

	private CapeCustomizePacket(UUID uuid, boolean enabled) {
		this.playerUUID = uuid;
		this.enabled = enabled;
	}

	private CapeCustomizePacket(UUID uuid, boolean enabled, ResourceLocation capeId) {
		this(uuid, enabled);
		this.capeId = capeId;
	}

	public CapeCustomizePacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readUUID(), buf.readBoolean());
		if (this.enabled) {
			this.capeId = buf.readResourceLocation();
		}
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeUUID(this.playerUUID);
		buf.writeBoolean(this.enabled);
		if (this.enabled) {
			buf.writeResourceLocation(this.capeId);
		}
	}

	public static @NotNull CapeCustomizePacket createDisablePacket(UUID uuid) {
		return new CapeCustomizePacket(uuid, false);
	}

	public static @NotNull CapeCustomizePacket createPacket(UUID uuid, @Nullable ResourceLocation capeId) {
		return new CapeCustomizePacket(uuid, !shouldDisable(CapeUtil.getCape(capeId).orElse(null)), capeId);
	}

	@Contract("_, _ -> new")
	public static @NotNull CapeCustomizePacket createPacket(UUID uuid, @NotNull Cape cape) {
		return new CapeCustomizePacket(uuid, !shouldDisable(cape), cape.registryId());
	}

	public static void sendCapeToAll(MinecraftServer server, UUID uuid, @Nullable ResourceLocation capeId) {
		CapeCustomizePacket frozenCapePacket = CapeCustomizePacket.createPacket(uuid, capeId);
		PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, frozenCapePacket));
	}

	public static boolean shouldDisable(Cape cape) {
		return cape == null || shouldDisable(cape.registryId()) || cape.texture() == null;
	}

	public static boolean shouldDisable(ResourceLocation capeId) {
		return capeId == null || capeId.equals(DUMMY);
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public ResourceLocation getCapeId() {
		return this.capeId;
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
