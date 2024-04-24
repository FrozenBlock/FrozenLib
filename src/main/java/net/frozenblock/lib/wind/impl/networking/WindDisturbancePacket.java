/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record WindDisturbancePacket(
	AABB affectedArea,
	Vec3 origin,
	WindDisturbanceLogic.SourceType disturbanceSourceType,
	ResourceLocation id,
	long posOrID

) implements CustomPacketPayload {
	public static final Type<WindDisturbancePacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("wind_disturbance_packet")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbancePacket> CODEC = StreamCodec.ofMember(WindDisturbancePacket::write, WindDisturbancePacket::new);

	public WindDisturbancePacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(
			new AABB(buf.readVec3(), buf.readVec3()),
			buf.readVec3(),
			buf.readEnum(WindDisturbanceLogic.SourceType.class),
			buf.readResourceLocation(),
			buf.readLong()
		);
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
		AABB affectedArea = this.affectedArea();
		buf.writeVec3(new Vec3(affectedArea.minX, affectedArea.minY, affectedArea.minZ));
		buf.writeVec3(new Vec3(affectedArea.maxX, affectedArea.maxY, affectedArea.maxZ));
		buf.writeVec3(this.origin());
		buf.writeEnum(this.disturbanceSourceType());
		buf.writeResourceLocation(this.id());
		buf.writeLong(this.posOrID());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
