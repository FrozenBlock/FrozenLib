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

import java.util.List;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import org.jetbrains.annotations.NotNull;

public record ImprovedGoalDebugPayload(int entityId, List<GoalDebugPayload.DebugGoal> goals) implements FabricPacket {
	public static final PacketType<ImprovedGoalDebugPayload> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("debug_goals"), ImprovedGoalDebugPayload::new
	);

	public ImprovedGoalDebugPayload(@NotNull FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readList(GoalDebugPayload.DebugGoal::new));
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeCollection(this.goals, (bufx, goal) -> goal.write(bufx));
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}

}
