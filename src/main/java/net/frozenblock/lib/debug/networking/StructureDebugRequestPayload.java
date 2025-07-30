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

package net.frozenblock.lib.debug.networking;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record StructureDebugRequestPayload(ChunkPos chunkPos) implements CustomPacketPayload {
	public static final Type<StructureDebugRequestPayload> PACKET_TYPE = new Type<>(FrozenLibConstants.id("debug_structure_request"));
	public static final StreamCodec<FriendlyByteBuf, StructureDebugRequestPayload> STREAM_CODEC = StreamCodec.ofMember(
		StructureDebugRequestPayload::write, StructureDebugRequestPayload::new
	);

	public StructureDebugRequestPayload(@NotNull FriendlyByteBuf buf) {
		this(buf.readChunkPos());
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeChunkPos(this.chunkPos);
	}

	public static void sendBack(ServerPlayer sender, ServerLevel serverLevel, ChunkPos chunkPos) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		if (!serverLevel.hasChunk(chunkPos.x, chunkPos.z)) return;

		LevelChunk chunk = serverLevel.getChunk(chunkPos.x, chunkPos.z);
		chunk.getAllStarts().values().forEach(
			structureStart -> sender.connection.send(new ClientboundCustomPayloadPacket(createStructurePayload(serverLevel, structureStart)))
		);
	}

	@Contract("_, _ -> new")
	private static @NotNull StructuresDebugPayload createStructurePayload(ServerLevel serverLevel, @NotNull StructureStart structureStart) {
		List<StructuresDebugPayload.PieceInfo> pieces = new ArrayList<>();

		for (int i = 0; i < structureStart.getPieces().size(); ++i) {
			pieces.add(new StructuresDebugPayload.PieceInfo(structureStart.getPieces().get(i).getBoundingBox(), i == 0));
		}

		return new StructuresDebugPayload(serverLevel.dimension(), structureStart.getBoundingBox(), pieces);
	}

	@Override
	public @NotNull Type<?> type() {
		return PACKET_TYPE;
	}
}
