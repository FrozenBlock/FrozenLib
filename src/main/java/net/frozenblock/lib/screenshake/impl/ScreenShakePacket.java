package net.frozenblock.lib.screenshake.impl;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;

public record ScreenShakePacket(
	float intensity,
	int duration,
	int falloffStart,
	double x,
	double y,
	double z,
	float maxDistance,
	int ticks
) implements FabricPacket {

	public static final PacketType<ScreenShakePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("screen_shake_packet"),
		ScreenShakePacket::new
	);

	public ScreenShakePacket(FriendlyByteBuf buf) {
		this(
			buf.readFloat(),
			buf.readInt(),
			buf.readInt(),
			buf.readDouble(),
			buf.readDouble(),
			buf.readDouble(),
			buf.readFloat(),
			buf.readInt()
		);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeFloat(this.intensity());
		buf.writeInt(this.duration());
		buf.writeInt(this.falloffStart());
		buf.writeDouble(this.x());
		buf.writeDouble(this.y());
		buf.writeDouble(this.z());
		buf.writeFloat(this.maxDistance());
		buf.writeInt(this.ticks());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
