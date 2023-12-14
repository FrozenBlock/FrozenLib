package net.frozenblock.lib.screenshake.impl.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;

public record EntityScreenShakePacket(
	int entityId,
	float intensity,
	int duration,
	int falloffStart,
	float maxDistance,
	int ticks
) implements FabricPacket {

	public static final PacketType<EntityScreenShakePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("screen_shake_entity_packet"),
		EntityScreenShakePacket::new
	);

	public EntityScreenShakePacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeFloat(this.intensity());
		buf.writeInt(this.duration());
		buf.writeInt(this.falloffStart());
		buf.writeFloat(this.maxDistance());
		buf.writeInt(this.ticks());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
