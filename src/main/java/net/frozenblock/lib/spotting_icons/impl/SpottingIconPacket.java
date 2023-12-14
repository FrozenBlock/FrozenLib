package net.frozenblock.lib.spotting_icons.impl;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SpottingIconPacket(
	int entityId,
	ResourceLocation texture,
	float startFade,
	float endFade,
	ResourceLocation restrictionID
) implements FabricPacket {

	public static final PacketType<SpottingIconPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("spotting_icon_packet"),
		SpottingIconPacket::new
	);

	public SpottingIconPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readResourceLocation(), buf.readFloat(), buf.readFloat(), buf.readResourceLocation());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeResourceLocation(this.texture());
		buf.writeFloat(this.startFade());
		buf.writeFloat(this.endFade());
		buf.writeResourceLocation(this.restrictionID());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
