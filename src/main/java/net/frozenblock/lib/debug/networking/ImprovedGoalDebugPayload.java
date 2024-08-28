package net.frozenblock.lib.debug.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ImprovedGoalDebugPayload(int entityId, List<GoalDebugPayload.DebugGoal> goals) implements CustomPacketPayload {
	public static final Type<ImprovedGoalDebugPayload> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("debug_goals")
	);

	public static final StreamCodec<FriendlyByteBuf, ImprovedGoalDebugPayload> STREAM_CODEC = StreamCodec.ofMember(
		ImprovedGoalDebugPayload::write, ImprovedGoalDebugPayload::new
	);

	public ImprovedGoalDebugPayload(@NotNull FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readList(GoalDebugPayload.DebugGoal::new));
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeCollection(this.goals, (bufx, goal) -> goal.write(bufx));
	}

	@Override
	public @NotNull Type<?> type() {
		return PACKET_TYPE;
	}
}
