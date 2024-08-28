package net.frozenblock.lib.debug.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ImprovedGameEventDebugPayload(ResourceKey<GameEvent> gameEventType, Vec3 pos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ImprovedGameEventDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
		ImprovedGameEventDebugPayload::write, ImprovedGameEventDebugPayload::new
	);
	public static final Type<ImprovedGameEventDebugPayload> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("debug_game_event")
	);

	private ImprovedGameEventDebugPayload(@NotNull FriendlyByteBuf buf) {
		this(buf.readResourceKey(Registries.GAME_EVENT), buf.readVec3());
	}

	private void write(@NotNull FriendlyByteBuf buf) {
		buf.writeResourceKey(this.gameEventType);
		buf.writeVec3(this.pos);
	}

	@Override
	public Type<ImprovedGameEventDebugPayload> type() {
		return PACKET_TYPE;
	}
}
