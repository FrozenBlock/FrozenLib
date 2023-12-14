package net.frozenblock.lib.item.impl.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;

public record ForcedCooldownPacket(
	Item item,
	int startTime,
	int endTime
) implements FabricPacket {

	public static final PacketType<ForcedCooldownPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("forced_cooldown_packet"),
		ForcedCooldownPacket::new
	);

	public ForcedCooldownPacket(FriendlyByteBuf buf) {
		this(buf.readById(BuiltInRegistries.ITEM), buf.readVarInt(), buf.readVarInt());
	}


	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.ITEM, this.item());
		buf.writeVarInt(this.startTime());
		buf.writeVarInt(this.endTime());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
