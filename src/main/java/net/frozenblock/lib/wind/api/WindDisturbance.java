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

package net.frozenblock.lib.wind.api;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindDisturbance<T> {
	public static final DisturbanceResult DUMMY_RESULT = new DisturbanceResult(0D, 0D, Vec3.ZERO);

	private final Optional<T> source;
	private final Vec3 origin;
	private final AABB affectedArea;
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
			if (disturbanceResult != null) {
				return disturbanceResult;
			}
		}
		return DUMMY_RESULT;
	}

	public boolean isWithinViewDistance(@NotNull ChunkTrackingView chunkTrackingView) {
		for (double xCorner : ImmutableList.of(this.affectedArea.minX, this.affectedArea.maxX)) {
			for (double zCorner : ImmutableList.of(this.affectedArea.minZ, this.affectedArea.maxZ)) {
				ChunkPos chunkPos = new ChunkPos(BlockPos.containing(xCorner, 0, zCorner));
				if (chunkTrackingView.isInViewDistance(chunkPos.x, chunkPos.z)) {
					return true;
				}
			}
		}
		return false;
	}

	public Optional<WindDisturbancePacket> toPacket() {
		ResourceLocation resourceLocation = Optional.ofNullable(FrozenRegistry.WIND_DISTURBANCE_LOGIC.getKey(this.disturbanceLogic))
			.orElseGet(() -> FrozenRegistry.WIND_DISTURBANCE_LOGIC_UNSYNCED.getKey(this.disturbanceLogic));

		if (resourceLocation != null) {
			return Optional.of(
				new WindDisturbancePacket(
					this.affectedArea,
					this.origin,
					this.getSourceTypeFromSource(),
					resourceLocation,
					this.encodePosOrID()
				)
			);
		}

		return Optional.empty();
	}

	private WindDisturbanceLogic.SourceType getSourceTypeFromSource() {
		if (this.source.isPresent()) {
			if (this.source.get() instanceof Entity) {
				return WindDisturbanceLogic.SourceType.ENTITY;
			} else if (this.source.get() instanceof BlockEntity) {
				return WindDisturbanceLogic.SourceType.BLOCK_ENTITY;
			}
		}
		return WindDisturbanceLogic.SourceType.NONE;
	}

	private long encodePosOrID() {
		if (this.source.isPresent()) {
			if (this.source.get() instanceof Entity entity) {
				return entity.getId();
			} else if (this.source.get() instanceof BlockEntity blockEntity) {
				return blockEntity.getBlockPos().asLong();
			}
		}
		return 0L;
	}

	public record DisturbanceResult(double strength, double weight, Vec3 wind) {
	}
}
