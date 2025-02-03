/*
 * Copyright (C) 2024-2025 FrozenBlock
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

import net.frozenblock.lib.FrozenLibConstants;
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
		FrozenLibConstants.id("debug_game_event")
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
