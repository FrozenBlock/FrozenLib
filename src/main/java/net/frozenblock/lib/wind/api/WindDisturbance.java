/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind.api;

import java.util.Optional;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindDisturbance<T> {
	public static final DisturbanceResult DUMMY_RESULT = new DisturbanceResult(0D, 0D, Vec3.ZERO);

	private final Optional<T> source;
	public final Vec3 origin;
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
