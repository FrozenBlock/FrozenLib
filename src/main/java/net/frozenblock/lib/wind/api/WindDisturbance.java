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

package net.frozenblock.lib.wind.api;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Used to create areas with custom, differing wind patterns.
 *
 * <p> Once added to a {@link WindManager} or {@link ClientWindManager}, will be used for one tick and cleared the next.
 * <p> Define the origin and affected area of a disturbance here, and define the logic used with a {@link WindDisturbanceLogic} instance.
 */
public class WindDisturbance<T> {
	public static final DisturbanceResult DUMMY_RESULT = new DisturbanceResult(0D, 0D, Vec3.ZERO);

	private final Optional<T> source;
	public final Vec3 origin;
	public final AABB affectedArea;
	private final WindDisturbanceLogic<T> disturbanceLogic;

    public WindDisturbance(Optional<T> source, Vec3 origin, AABB affectedArea, WindDisturbanceLogic<T> disturbanceLogic) {
		this.source = source;
        this.origin = origin;
        this.affectedArea = affectedArea;
        this.disturbanceLogic = disturbanceLogic;
    }

	public DisturbanceResult calculateDisturbanceResult(Level level, Vec3 windTarget) {
		if (this.affectedArea.contains(windTarget)) {
			DisturbanceResult disturbanceResult = this.disturbanceLogic.getLogic().calculateDisturbanceResult(
				this.source,
				level,
				this.origin,
				this.affectedArea,
				windTarget
			);
			if (disturbanceResult != null) return disturbanceResult;
		}
		return DUMMY_RESULT;
	}

	public boolean isWithinViewDistance(ChunkTrackingView chunkTrackingView) {
		for (double xCorner : ImmutableList.of(this.affectedArea.minX, this.affectedArea.maxX)) {
			for (double zCorner : ImmutableList.of(this.affectedArea.minZ, this.affectedArea.maxZ)) {
				final ChunkPos chunkPos = new ChunkPos(BlockPos.containing(xCorner, 0, zCorner));
				if (chunkTrackingView.isInViewDistance(chunkPos.x, chunkPos.z)) return true;
			}
		}
		return false;
	}

	public Optional<WindDisturbancePacket> toPacket() {
		final Identifier identifier = Optional.ofNullable(FrozenLibRegistries.WIND_DISTURBANCE_LOGIC.getKey(this.disturbanceLogic))
			.orElseGet(() -> FrozenLibRegistries.WIND_DISTURBANCE_LOGIC_UNSYNCED.getKey(this.disturbanceLogic));
		if (identifier == null) return Optional.empty();

		return Optional.of(
			new WindDisturbancePacket(
				this.affectedArea,
				this.origin,
				this.getSourceTypeFromSource(),
				identifier,
				this.encodePosOrID(this.origin)
			)
		);
	}

	private WindDisturbanceLogic.SourceType getSourceTypeFromSource() {
		if (this.source.isEmpty()) return WindDisturbanceLogic.SourceType.NONE;
		if (this.source.get() instanceof Entity) return WindDisturbanceLogic.SourceType.ENTITY;
		if (this.source.get() instanceof BlockEntity) return WindDisturbanceLogic.SourceType.BLOCK_ENTITY;
		if (this.source.get() instanceof Block) return WindDisturbanceLogic.SourceType.BLOCK;
		return WindDisturbanceLogic.SourceType.NONE;
	}

	private long encodePosOrID(Vec3 origin) {
		if (this.source.isEmpty()) return 0L;
		if (this.source.get() instanceof Entity entity) return entity.getId();
		if (this.source.get() instanceof BlockEntity blockEntity) return blockEntity.getBlockPos().asLong();
		if (this.source.get() instanceof Block) return BlockPos.containing(origin).asLong();
		return 0L;
	}

	public record DisturbanceResult(double strength, double weight, Vec3 wind) {
	}
}
