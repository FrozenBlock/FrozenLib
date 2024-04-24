/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.wind.impl.networking;

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
