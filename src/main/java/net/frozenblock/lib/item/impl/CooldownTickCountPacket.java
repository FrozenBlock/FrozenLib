package net.frozenblock.lib.item.impl;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;

public record CooldownTickCountPacket(int count) implements FabricPacket {

	public static final PacketType<CooldownTickCountPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("cooldown_tick_count_packet"),
		CooldownTickCountPacket::new
	);

	public CooldownTickCountPacket(FriendlyByteBuf buf) {
		this(buf.readInt());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(this.count());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
