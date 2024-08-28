package net.frozenblock.lib.debug.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record GoalDebugRemovePayload(int entityId) implements CustomPacketPayload {
	public static final Type<GoalDebugRemovePayload> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("debug_goals_remove")
	);

	public static final StreamCodec<FriendlyByteBuf, GoalDebugRemovePayload> STREAM_CODEC = StreamCodec.ofMember(
		GoalDebugRemovePayload::write, GoalDebugRemovePayload::new
	);

	public GoalDebugRemovePayload(@NotNull FriendlyByteBuf buf) {
		this(buf.readVarInt());
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	@Override
	public @NotNull Type<?> type() {
		return PACKET_TYPE;
	}
}
