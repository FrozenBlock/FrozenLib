package net.frozenblock.lib.wind.impl;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record WindSyncPacket(
	long windTime,
	long seed,
	boolean override,
	Vec3 commandWind
) implements CustomPacketPayload {

	public static final Type<WindSyncPacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("wind_sync_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, WindSyncPacket> CODEC = StreamCodec.ofMember(WindSyncPacket::write, WindSyncPacket::create);

	public static WindSyncPacket create(@NotNull FriendlyByteBuf buf) {
        return new WindSyncPacket(
			buf.readLong(),
			buf.readLong(),
			buf.readBoolean(),
			buf.readVec3()
		);
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeLong(this.windTime());
		buf.writeLong(this.seed());
		buf.writeBoolean(this.override());
		buf.writeVec3(this.commandWind());
	}

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}
