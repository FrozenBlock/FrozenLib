package net.frozenblock.lib.spotting_icons.impl;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;

public record SpottingIconRemovePacket(
	int entityId
) implements FabricPacket {

	public static final PacketType<SpottingIconRemovePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("spotting_icon_remove_packet"),
		SpottingIconRemovePacket::new
	);

	public SpottingIconRemovePacket(FriendlyByteBuf buf) {
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
