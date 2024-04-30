/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.api.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

public record MovingFadingDistanceSwitchingRestrictionSoundPacket(
	int id,
	Holder<SoundEvent> closeSound,
	Holder<SoundEvent> farSound,
	SoundSource category,
	float volume,
	float pitch,
	float fadeDist,
	float maxDist,
	ResourceLocation predicateId,
	boolean stopOnDeath,
	boolean looping
) implements CustomPacketPayload {

	public static final Type<MovingFadingDistanceSwitchingRestrictionSoundPacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("moving_fading_restriction_sound_packet")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, MovingFadingDistanceSwitchingRestrictionSoundPacket> CODEC = StreamCodec.ofMember(MovingFadingDistanceSwitchingRestrictionSoundPacket::write, MovingFadingDistanceSwitchingRestrictionSoundPacket::new);

	public MovingFadingDistanceSwitchingRestrictionSoundPacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readFloat(),
			buf.readFloat(),
			buf.readResourceLocation(),
			buf.readBoolean(),
			buf.readBoolean()
		);
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(this.id());
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.closeSound());
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.farSound());
		buf.writeEnum(this.category());
		buf.writeFloat(this.volume());
		buf.writeFloat(this.pitch());
		buf.writeFloat(this.fadeDist());
		buf.writeFloat(this.maxDist());
		buf.writeResourceLocation(predicateId());
		buf.writeBoolean(this.stopOnDeath());
		buf.writeBoolean(this.looping());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
