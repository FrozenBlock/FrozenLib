/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.networking;

import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.event.api.events.PlayerJoinEvents;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.sound.impl.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.RelativeMovingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;

// TODO PORT THESE TO NEOFORGE
public final class FrozenNetworkingFabric {

	public static void registerNetworking() {
		final var networking = FrozenLibInitPlatformUtils.NETWORKING;

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			CapeUtil.sendCapeReposToPlayer(player);
		});

		networking.registerS2CPayloadType(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		networking.registerS2CPayloadType(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		networking.registerS2CPayloadType(RelativeMovingSoundPacket.PACKET_TYPE, RelativeMovingSoundPacket.CODEC);
		networking.registerS2CPayloadType(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
		networking.registerS2CPayloadType(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
		networking.registerS2CPayloadType(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		networking.registerS2CPayloadType(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		networking.registerS2CPayloadType(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);

		// DEBUG
		networking.registerS2CPayloadType(WindAccessPacket.TYPE, WindAccessPacket.STREAM_CODEC);
	}
}
