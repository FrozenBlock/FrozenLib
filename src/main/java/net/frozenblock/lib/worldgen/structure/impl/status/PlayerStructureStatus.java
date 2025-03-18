/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.worldgen.structure.impl.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PlayerStructureStatus {
	public static final StreamCodec<FriendlyByteBuf, PlayerStructureStatus> STREAM_CODEC = new StreamCodec<>() {
		@NotNull
		@Override
		public PlayerStructureStatus decode(@NotNull FriendlyByteBuf buf) {
			return new PlayerStructureStatus(buf.readResourceLocation(), buf.readBoolean());
		}

		@Override
		public void encode(@NotNull FriendlyByteBuf buf, @NotNull PlayerStructureStatus playerStructureStatus) {
			buf.writeResourceLocation(playerStructureStatus.structure);
			buf.writeBoolean(playerStructureStatus.insidePiece);
		}
	};

	private final ResourceLocation structure;
	private boolean insidePiece;

	public PlayerStructureStatus(ResourceLocation structure, boolean insidePiece) {
		this.structure = structure;
		this.insidePiece = insidePiece;
	}

	public ResourceLocation getStructure() {
		return this.structure;
	}

	public boolean isInsidePiece() {
		return this.insidePiece;
	}

	public void setInsidePiece(boolean insidePiece) {
		this.insidePiece = insidePiece;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerStructureStatus other) {
			return this.structure.equals(other.structure) && this.insidePiece == other.insidePiece;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int i = 1;
		i = 31 * i + this.structure.hashCode();
		return 31 * i + Boolean.hashCode(this.insidePiece);
	}
}
