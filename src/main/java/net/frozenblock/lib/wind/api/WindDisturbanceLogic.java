/*
 * Copyright (C) 2024 FrozenBlock
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

import java.util.Optional;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.WindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class WindDisturbanceLogic<T> {
	public static final ResourceLocation DEFAULT_ID = FrozenSharedConstants.id("default");
	public static final ResourceLocation BREEZE = FrozenSharedConstants.id("breeze");
	public static final ResourceLocation WIND_CHARGE = FrozenSharedConstants.id("wind_charge");
	private final DisturbanceLogic<T> disturbanceLogic;

	public WindDisturbanceLogic(DisturbanceLogic<T> disturbanceLogic) {
		this.disturbanceLogic = disturbanceLogic;
	}

	public DisturbanceLogic getLogic() {
		return this.disturbanceLogic;
	}

	@FunctionalInterface
	public interface DisturbanceLogic<T> {
		WindDisturbance.DisturbanceResult calculateDisturbanceResult(Optional<T> source, Level level, Vec3 windOrigin, AABB affectedArea, Vec3 windTarget);
	}

	public static Optional<WindDisturbanceLogic<?>> getWindDisturbanceLogic(ResourceLocation id) {
        if (id != null) {
            if (FrozenRegistry.WIND_DISTURBANCE_LOGIC.containsKey(id)) {
				WindDisturbanceLogic<?> disturbanceLogic = FrozenRegistry.WIND_DISTURBANCE_LOGIC.get(id);
				if (disturbanceLogic != null) {
					return Optional.of(disturbanceLogic);
				}
			} else if (FrozenRegistry.WIND_DISTURBANCE_LOGIC_UNSYNCED.containsKey(id)) {
				WindDisturbanceLogic<?> disturbanceLogic = FrozenRegistry.WIND_DISTURBANCE_LOGIC_UNSYNCED.get(id);
				if (disturbanceLogic != null) {
					return Optional.of(disturbanceLogic);
				}
			}
			FrozenSharedConstants.LOGGER.error("Unable to find wind disturbance logic {}!", id);
        }
        return Optional.empty();
    }

	@NotNull
	@Contract(pure = true)
	public static DisturbanceLogic<?> defaultPredicate() {
		return (source, level, windOrigin, affectedArea, windTarget) -> WindDisturbance.DUMMY_RESULT;
	}

	public static void init() {
		register(DEFAULT_ID, defaultPredicate());
		register(BREEZE, breeze());
		register(WIND_CHARGE, windCharge());
	}


	public static <T> void register(ResourceLocation id, DisturbanceLogic<T> predicate) {
		Registry.register(FrozenRegistry.WIND_DISTURBANCE_LOGIC, id, new WindDisturbanceLogic<>(predicate));
	}

	public static <T> void registerUnsynced(ResourceLocation id, DisturbanceLogic<T> predicate) {
		Registry.register(FrozenRegistry.WIND_DISTURBANCE_LOGIC_UNSYNCED, id, new WindDisturbanceLogic<>(predicate));
	}

    public enum SourceType {
		ENTITY,
		BLOCK_ENTITY,
		NONE
	}

	private static final double WIND_RANGE_BREEZE = 6D;
	private static final double WIND_RANGE_WIND_CHARGE = 5D;

	@NotNull
	@Contract(pure = true)
	private static DisturbanceLogic<Breeze> breeze() {
		return (source, level, windOrigin, affectedArea, windTarget) -> {
			if (source.isPresent()) {
				double distance = windOrigin.distanceTo(windTarget);
				if (distance <= WIND_RANGE_BREEZE) {
					Vec3 breezeLookVec = source.get().getForward();
					Vec3 differenceInPoses = windOrigin.subtract(windTarget);
					double scaledDistance = (WIND_RANGE_BREEZE - distance) / WIND_RANGE_BREEZE;
					double strengthFromDistance = Mth.clamp((WIND_RANGE_BREEZE - distance) / (WIND_RANGE_BREEZE * 0.75D), 0D, 1D);
					double angleBetween = AdvancedMath.getAngleBetweenXZ(breezeLookVec, differenceInPoses);

					double x = Math.cos((angleBetween * Math.PI) / 180D);
					double z = -Math.sin((angleBetween * Math.PI) / 180D);
					x = -Mth.lerp(scaledDistance, (x - (differenceInPoses.x * 0.45D)) * 0.5D, x);
					z = -Mth.lerp(scaledDistance, (z - (differenceInPoses.z * 0.45D)) * 0.5D, z);

					Vec3 windVec = new Vec3(x, strengthFromDistance, z).scale(1D);
					return new WindDisturbance.DisturbanceResult(
						strengthFromDistance,
						WIND_RANGE_BREEZE - distance,
						windVec
					);
				}
			}
			return null;
		};
	}

	@NotNull
	@Contract(pure = true)
	private static DisturbanceLogic<WindCharge> windCharge() {
		return (source, level, windOrigin, affectedArea, windTarget) -> {
			if (source.isPresent()) {
				double distance = windOrigin.distanceTo(windTarget);
				if (distance <= WIND_RANGE_WIND_CHARGE) {
					Vec3 chargeMovement = source.get().getDeltaMovement();
					double strengthFromDistance = Mth.clamp((WIND_RANGE_WIND_CHARGE - distance) / (WIND_RANGE_WIND_CHARGE * 0.5D), 0D, 1D);
					Vec3 windVec = new Vec3(chargeMovement.x, chargeMovement.y, chargeMovement.z).scale(3D * strengthFromDistance);
					return new WindDisturbance.DisturbanceResult(
						strengthFromDistance,
						(WIND_RANGE_WIND_CHARGE - distance) * 2D,
						windVec
					);
				}
			}
			return null;
		};
	}
}
