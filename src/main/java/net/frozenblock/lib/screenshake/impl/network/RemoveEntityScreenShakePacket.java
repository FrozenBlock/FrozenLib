package net.frozenblock.lib.screenshake.impl.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;

public record RemoveEntityScreenShakePacket(
	int entityId
) implements FabricPacket {

	public static final PacketType<RemoveEntityScreenShakePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("remove_entity_screen_shakes_packet"),
		RemoveEntityScreenShakePacket::new
	);

	public RemoveEntityScreenShakePacket(FriendlyByteBuf buf) {
		this(buf.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
