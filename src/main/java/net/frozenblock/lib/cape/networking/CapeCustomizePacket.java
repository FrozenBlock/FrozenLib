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
