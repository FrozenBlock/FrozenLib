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

package net.frozenblock.lib.cape.impl.networking;

import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CapeCustomizePacket implements CustomPacketPayload {
	private static final Identifier DUMMY = FrozenLibConstants.id("dummy");
	public static final Type<CapeCustomizePacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("cape_packet"));
	public static final StreamCodec<FriendlyByteBuf, CapeCustomizePacket> CODEC = StreamCodec.ofMember(CapeCustomizePacket::write, CapeCustomizePacket::new);

	private final UUID playerUUID;
	private final boolean enabled;
	private Identifier capeId = null;

	private CapeCustomizePacket(UUID uuid, boolean enabled) {
		this.playerUUID = uuid;
		this.enabled = enabled;
	}

	private CapeCustomizePacket(UUID uuid, boolean enabled, Identifier capeId) {
		this(uuid, enabled);
		this.capeId = capeId;
	}

	public CapeCustomizePacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readUUID(), buf.readBoolean());
		if (this.enabled) this.capeId = buf.readIdentifier();
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeUUID(this.playerUUID);
		buf.writeBoolean(this.enabled);
		if (this.enabled) buf.writeIdentifier(this.capeId);
	}

	public static @NotNull CapeCustomizePacket createDisablePacket(UUID uuid) {
		return new CapeCustomizePacket(uuid, false);
	}

	public static @NotNull CapeCustomizePacket createPacket(UUID uuid, @Nullable Identifier capeId) {
		return new CapeCustomizePacket(uuid, !shouldDisable(CapeUtil.getCape(capeId).orElse(null)), capeId);
	}

	@Contract("_, _ -> new")
	public static @NotNull CapeCustomizePacket createPacket(UUID uuid, @NotNull Cape cape) {
		return new CapeCustomizePacket(uuid, !shouldDisable(cape), cape.registryId());
	}

	public static void sendCapeToAll(MinecraftServer server, UUID uuid, @Nullable Identifier capeId) {
		CapeCustomizePacket frozenCapePacket = CapeCustomizePacket.createPacket(uuid, capeId);
		PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, frozenCapePacket));
	}

	public static boolean shouldDisable(Cape cape) {
		return cape == null || shouldDisable(cape.registryId()) || cape.texture() == null;
	}

	public static boolean shouldDisable(Identifier capeId) {
		return capeId == null || capeId.equals(DUMMY);
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	@Nullable
	public Identifier getCapeId() {
		return this.capeId;
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
