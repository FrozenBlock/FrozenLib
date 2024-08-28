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
