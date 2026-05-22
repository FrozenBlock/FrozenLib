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

import java.util.List;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.Structure;

public record StructureStatus(Holder<Structure> structure, boolean insidePiece) {
	public static final StreamCodec<RegistryFriendlyByteBuf, StructureStatus> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.holderRegistry(Registries.STRUCTURE), StructureStatus::structure,
		ByteBufCodecs.BOOL, StructureStatus::insidePiece,
		StructureStatus::new
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, List<StructureStatus>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
	public static final AttachmentType<List<StructureStatus>> ATTACHMENT_TYPE = AttachmentRegistry.create(
		FrozenLibConstants.id("structure_statuses"),
		builder -> builder.syncWith(LIST_STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
	);

	public static Optional<StructureStatus> getProminentStructureStatus(Player player) {
		if (player == null) return Optional.empty();
		final List<StructureStatus> structureStatuses = player.getAttachedOrElse(StructureStatus.ATTACHMENT_TYPE, ImmutableList.of());
		return Optional.ofNullable(structureStatuses.stream()
			.filter(StructureStatus::insidePiece)
			.findFirst()
			.orElseGet(() -> structureStatuses.stream().findFirst().orElse(null))
		);
	}
}
