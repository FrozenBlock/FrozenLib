/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.cape.impl;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentSyncPredicate;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record Cape(Identifier id, Component name, CapeTexture texture, Optional<List<UUID>> allowedPlayers) {
	public static final StreamCodec<ByteBuf, Optional<Cape>> NETWORK_CODEC = StreamCodec.of(Cape::writeToStream, Cape::createFromStream);
	public static final DataAttachmentType<Optional<Cape>> ATTACHMENT_TYPE = DataAttachmentType.create(
		FrozenLibConstants.id("cape"),
		builder -> {
			builder.syncWith(NETWORK_CODEC, DataAttachmentSyncPredicate.all());
			builder.copyOnDeath();
		}
	);

	public Cape(Identifier id, Component name, Identifier texture, Optional<List<UUID>> allowedPlayers) {
		this(id, name, new CapeTexture(texture), allowedPlayers);
	}

	public static void init() {}

	public boolean dummy() {
		return this.id.getPath().equals("dummy");
	}

	private static void writeToStream(ByteBuf output, Optional<Cape> cape) {
		ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(output, cape.map(Cape::id));
	}

	public static Optional<Cape> createFromStream(ByteBuf input) {
		return ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(input).flatMap(CapeUtil::getCape);
	}

	public static class CapeTexture implements ClientAsset.Texture {
		private final Identifier texture;

		private CapeTexture(Identifier texture) {
			this.texture = texture;
		}

		@Override
		public Identifier texturePath() {
			return this.texture;
		}

		@Override
		public Identifier id() {
			return this.texture;
		}
	}
}
