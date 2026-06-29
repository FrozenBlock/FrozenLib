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

package net.frozenblock.lib.levelgen.structure.impl.status;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.data.DataAttachmentSyncPredicate;
import net.frozenblock.lib.platform.api.data.DataAttachmentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record StructureStatus(Identifier structure, boolean insidePiece) {
	public static final StreamCodec<FriendlyByteBuf, StructureStatus> STREAM_CODEC = StreamCodec.composite(
		Identifier.STREAM_CODEC, StructureStatus::structure,
		ByteBufCodecs.BOOL, StructureStatus::insidePiece,
		StructureStatus::new
	);
	public static final StreamCodec<FriendlyByteBuf, List<StructureStatus>> STREAM_CODEC_LIST = STREAM_CODEC.apply(ByteBufCodecs.list());
	public static final DataAttachmentType<List<StructureStatus>> ATTACHMENT_TYPE = DataAttachmentType.create(
		FrozenLibConstants.id("structure_statuses"),
		builder -> builder.syncWith(STREAM_CODEC_LIST, DataAttachmentSyncPredicate.targetOnly())
	);

	public static void init() {}

	public static Optional<StructureStatus> getProminentStructureStatus(Player player) {
		if (player == null) return Optional.empty();
		final List<StructureStatus> statuses = ATTACHMENT_TYPE.getAttachedOrGet(player, ImmutableList::of);
		return Optional.ofNullable(statuses.stream()
			.filter(StructureStatus::insidePiece)
			.findFirst()
			.orElseGet(() -> statuses.stream().findFirst().orElse(null))
		);
	}
}
