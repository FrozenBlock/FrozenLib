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

package net.frozenblock.lib.spotting_icons.impl;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SpottingIconPacket(
	int entityId,
	ResourceLocation texture,
	float startFade,
	float endFade,
	ResourceLocation restrictionID
) implements CustomPacketPayload {

	public static final Type<SpottingIconPacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("spotting_icon_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, SpottingIconPacket> CODEC = StreamCodec.ofMember(SpottingIconPacket::write, SpottingIconPacket::new);

	public SpottingIconPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readResourceLocation(), buf.readFloat(), buf.readFloat(), buf.readResourceLocation());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeResourceLocation(this.texture());
		buf.writeFloat(this.startFade());
		buf.writeFloat(this.endFade());
		buf.writeResourceLocation(this.restrictionID());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
