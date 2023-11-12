/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.gravity.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

public final class GravityAPI {
    private GravityAPI() {}

    private static final Map<ResourceKey<DimensionType>, List<GravityBelt<?>>> GRAVITY_BELTS = new HashMap<>();

    public static void register(ResourceKey<DimensionType> dimension, GravityBelt<?> gravityBelt) {
		getAllBelts(dimension).add(gravityBelt);
    }

	@NotNull
    public static List<GravityBelt<?>> getAllBelts(ResourceKey<DimensionType> dimension) {
        return GRAVITY_BELTS.computeIfAbsent(dimension, dimension1 -> new ArrayList<>());
    }

    public static List<GravityBelt<?>> getAllBelts(Level level) {
        return getAllBelts(level.dimensionTypeId());
    }

    public static double calculateGravity(ResourceKey<DimensionType> dimension, double y) {
        if (GRAVITY_BELTS.containsKey(dimension)) {
            Optional<GravityBelt<?>> optionalGravityBelt = getAffectingGravityBelt(GRAVITY_BELTS.get(dimension), y);
            if (optionalGravityBelt.isPresent()) {
                GravityBelt<?> belt = optionalGravityBelt.get();
                return belt.getGravity(null, y);
            }
        }
        return 1.0;
    }

    public static double calculateGravity(Level level, double y) {
        return calculateGravity(level.dimensionTypeId(), y);
    }

    public static double calculateGravity(Entity entity) {
        ResourceKey<DimensionType> dimension = entity.level().dimensionTypeId();
        if (GRAVITY_BELTS.containsKey(dimension)) {
            double y = entity.getY();
            Optional<GravityBelt<?>> optionalGravityBelt = getAffectingGravityBelt(GRAVITY_BELTS.get(dimension), y);
            if (optionalGravityBelt.isPresent()) {
                GravityBelt<?> belt = optionalGravityBelt.get();
                // at some point add extensions or something
                return belt.getGravity(entity, y);
            }
        }
        return 1.0;
    }

    public static Direction getGravityDirection(Entity entity) {
        return calculateGravity(entity) >= 0 ? Direction.DOWN : Direction.UP;
    }

    public static boolean isGravityDown(Entity entity) {
        return getGravityDirection(entity) == Direction.DOWN;
    }

    public static Optional<GravityBelt<?>> getAffectingGravityBelt(List<GravityBelt<?>> belts, double y) {
        Optional<GravityBelt<?>> optionalGravityBelt = Optional.empty();
        for (GravityBelt<?> belt : belts) {
            if (belt.affectsPosition(y)) {
                optionalGravityBelt = Optional.of(belt);
                break;
            }
        }
        return optionalGravityBelt;
    }

}
