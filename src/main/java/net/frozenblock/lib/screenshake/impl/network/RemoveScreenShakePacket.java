package net.frozenblock.lib.screenshake.impl.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;

public record RemoveScreenShakePacket() implements FabricPacket {

	public static final PacketType<RemoveScreenShakePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("remove_screen_shakes_packet"),
		RemoveScreenShakePacket::new
	);

	public RemoveScreenShakePacket(FriendlyByteBuf buf) {
		this();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
