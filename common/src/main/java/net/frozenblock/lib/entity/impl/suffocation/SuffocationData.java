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

package net.frozenblock.lib.entity.impl.suffocation;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SuffocationData(Map<Holder<SuffocationType>, Integer> units) {
	public static final SuffocationData EMPTY = new SuffocationData(Map.of());

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<Holder<SuffocationType>, Integer>> UNITS_STREAM_CODEC =
		ByteBufCodecs.map(HashMap::new, SuffocationType.STREAM_CODEC, ByteBufCodecs.VAR_INT);

	public static final StreamCodec<RegistryFriendlyByteBuf, SuffocationData> STREAM_CODEC = StreamCodec.composite(
		UNITS_STREAM_CODEC, SuffocationData::units,
		SuffocationData::new
	);

	public static final AttachmentType<SuffocationData> ATTACHMENT = AttachmentRegistry.create(
		FrozenLibConstants.id("suffocation_data"),
		builder -> builder.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all())
	);

	public static void init() {}

	public int getUnits(Holder<SuffocationType> type, int fallback) {
		return this.units.getOrDefault(type, fallback);
	}

	public boolean isEmpty() {
		return this.units.isEmpty();
	}
}
