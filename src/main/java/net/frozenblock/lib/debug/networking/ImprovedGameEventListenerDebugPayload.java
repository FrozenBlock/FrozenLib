package net.frozenblock.lib.debug.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.gameevent.PositionSource;
import org.jetbrains.annotations.NotNull;

public record ImprovedGameEventListenerDebugPayload(PositionSource listenerPos, int listenerRange) implements CustomPacketPayload {
	public static final Type<ImprovedGameEventListenerDebugPayload> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("debug_game_event_listener")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ImprovedGameEventListenerDebugPayload> STREAM_CODEC = StreamCodec.composite(
		PositionSource.STREAM_CODEC,
		ImprovedGameEventListenerDebugPayload::listenerPos,
		ByteBufCodecs.VAR_INT,
		ImprovedGameEventListenerDebugPayload::listenerRange,
		ImprovedGameEventListenerDebugPayload::new
	);

	@Override
	public @NotNull Type<?> type() {
		return PACKET_TYPE;
	}
}
