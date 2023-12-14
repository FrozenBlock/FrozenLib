package net.frozenblock.lib.item.impl.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;

public record CooldownChangePacket(
	Item item,
	int additional
) implements FabricPacket {

	public static final PacketType<CooldownChangePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("cooldown_change_packet"),
		CooldownChangePacket::new
	);

	public CooldownChangePacket(FriendlyByteBuf buf) {
		this(buf.readById(BuiltInRegistries.ITEM), buf.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.ITEM, this.item());
		buf.writeVarInt(this.additional());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
