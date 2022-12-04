/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.math.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * Adds more math operations.
 *
 * @author LiukRast (2021-2022)
 * @author FrozenBlock (2022)
 * @since 4.0
 */
public final class AdvancedMath {

	private AdvancedMath() {
		throw new UnsupportedOperationException("AdvancedMath contains only static declarations.");
	}

    public static float range(final float min, final float max,
                              final float number) {
        return (number * max) + min;
    }

    public static double randomPosNeg() {
        return Math.random() * (Math.random() >= 0.5 ? 1 : -1);
    }


    public static boolean squareBetween(
            final int x,
            final int z,
            final int between1,
            final int between2
    ) {
        boolean cond1 = x > between1 && x < between2;
        boolean cond2 = z > between1 && z < between2;
        return cond1 && cond2;
    }

    public static BlockPos offset(final BlockPos pos, final Direction dir, final int a) {
        return switch (dir) {
            case WEST -> pos.west(a);
            case EAST -> pos.east(a);
            case SOUTH -> pos.south(a);
            case NORTH -> pos.north(a);
            case UP -> pos.above(a);
            case DOWN -> pos.below(a);
        };
    }

    public static BlockPos offset(final BlockPos pos, final Direction dir) {
        return offset(pos, dir, 1);
    }

    public static int waterToHollowedProperty(final int value) {
        if (value > 8) {
            return 8;
        } else if (value < 0) {
            return -1;
        } else {
            return value;
        }
    }

    public static int waterLevelReduce(final int value) {
        if (value < 8) {
            return value + 1;
        } else {
            return 8;
        }
    }

    public static double cutCos(double value, double offset, boolean inverse) {
        double equation = Math.cos(value);
        if (!inverse) {
            return Math.max(equation, offset);
        } else {
            return Math.max(-equation, offset);
        }
    }

    /**
     * @param axis The axis that should be used to determine a random direction.
     * @return A random {@linkplain Direction} on a specific {@linkplain Direction.Axis}.
     */
    public static Direction randomDir(final Direction.Axis axis) {
        double random = Math.random();
		switch (axis) {
			case X -> {
				return random > 0.5 ? Direction.EAST : Direction.WEST;
			}
			case Y -> {
				return random > 0.5 ? Direction.UP : Direction.DOWN;
			}
			default -> {
				return random > 0.5 ? Direction.NORTH : Direction.SOUTH;
			}
		}
    }

	public static Vec3 rotateAboutXZ(Vec3 original, double distanceFrom, double angle) {
		double calcAngle = angle * (Math.PI / 180);
		Vec3 offsetVec = original.add(distanceFrom, 0, distanceFrom);
		double originX = original.x;
		double originZ = original.z;
		double distancedX = offsetVec.x;
		double distancedZ = offsetVec.z;
		double x = originX + (distancedX - originX) * Math.cos(calcAngle) - (distancedZ - originZ) * Math.sin(calcAngle);
		double z = originZ + (distancedX - originX) * Math.sin(calcAngle) + (distancedZ - originZ) * Math.cos(calcAngle);
		return new Vec3(x, original.y, z);
	}
}
