/*
 * Copyright (C) 2024 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
