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

package net.frozenblock.lib.math.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds more math operations.
 *
 * @author LiukRast (2021-2022)
 * @author FrozenBlock (2022)
 * @since 4.0
 */
@UtilityClass
public class AdvancedMath {

	@Contract(" -> new")
	@NotNull
	public static RandomSource random() {
		return RandomSource.create();
	}

    public static float range(final float min, final float max,
                              final float number) {
        return (number * max) + min;
    }

    public static double randomPosNeg() {
        return random().nextDouble() * (random().nextDouble() >= 0.5 ? 1 : -1);
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

    public static BlockPos offset(final BlockPos pos, final @NotNull Direction dir, final int a) {
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

	public static int factorial(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Factorial of negative numbers is undefined");
		}
		int result = 1;
		for (int i = 2; i <= n; i++) {
			result *= i;
		}
		return result;
	}

	public static int permutations(int n, int r) {
		if (n < 0 || r < 0 || r > n) {
			throw new IllegalArgumentException("Invalid input: n must be non-negative, r must be non-negative and not greater than n");
		}
		int result = 1;
		for (int i = n; i > n - r; i--) {
			result *= i;
		}
		return result;
	}

	public static int combinations(int n, int r) {
		if (n < 0 || r < 0 || r > n) {
			throw new IllegalArgumentException("Invalid input: n must be non-negative, r must be non-negative and not greater than n");
		}
		int numerator = 1;
		for (int i = n; i > n - r; i--) {
			numerator *= i;
		}
		int denominator = 1;
		for (int i = r; i > 0; i--) {
			denominator *= i;
		}
		return numerator / denominator;
	}

	/**
	 * Solves a quadratic equation of the form ax^2 + bx + c = 0.
	 *
	 * @param a the coefficient of x^2 (must be non-zero)
	 * @param b the coefficient of x
	 * @param c the constant term
	 * @return an array containing the real roots of the equation, or null if no real roots exist
	 * @throws IllegalArgumentException if a is zero
	 */
	public static double @Nullable [] solveQuadraticEquation(double a, double b, double c) {
		if (a == 0) {
			throw new IllegalArgumentException("a cannot be zero");
		}
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			return null;
		} else if (discriminant == 0) {
			double root = -b / (2 * a);
			return new double[]{root};
		} else {
			double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
			double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
			return new double[]{root1, root2};
		}
	}

	/**
	 * Calculates the greatest common divisor (GCD) of two numbers using the Euclidean algorithm.
	 *
	 * @param a the first number
	 * @param b the second number
	 * @return the GCD of a and b
	 */
	public static int greatestCommonDivisor(int a, int b) {
		if (a == 0 || b == 0) {
			return Math.abs(a + b); // GCD(0, b) == b; GCD(a, 0) == a; GCD(0, 0) == 0
		}

		while (b != 0) {
			int temp = b;
			b = a % b;
			a = temp;
		}

		return Math.abs(a);
	}

	/**
	 * @param axis The axis that should be used to determine a random direction.
	 * @return A random {@linkplain Direction} on a specific {@linkplain Direction.Axis}.
	 */
	@NotNull
    public static Direction randomDir(@NotNull final Direction.Axis axis) {
        double random = random().nextDouble();
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

	@NotNull
	public static Vec3 rotateAboutXZ(@NotNull Vec3 original, double distanceFrom, double angle) {
		double calcAngle = angle * (Math.PI / 180D);
		Vec3 offsetVec = original.add(distanceFrom, 0, distanceFrom);
		double originX = original.x;
		double originZ = original.z;
		double distancedX = offsetVec.x;
		double distancedZ = offsetVec.z;
		double x = originX + (distancedX - originX) * Math.cos(calcAngle) - (distancedZ - originZ) * Math.sin(calcAngle);
		double z = originZ + (distancedX - originX) * Math.sin(calcAngle) + (distancedZ - originZ) * Math.cos(calcAngle);
		return new Vec3(x, original.y, z);
	}

	@NotNull
	public static Vec3 rotateAboutX(@NotNull Vec3 original, double distanceFrom, double angle) {
		double calcAngle = angle * (Math.PI / 180D);
		Vec3 offsetVec = original.add(distanceFrom, 0, 0);
		double originX = original.x;
		double originZ = original.z;
		double distancedX = offsetVec.x;
		double distancedZ = offsetVec.z;
		double x = originX + (distancedX - originX) * Math.cos(calcAngle) - (distancedZ - originZ) * Math.sin(calcAngle);
		double z = originZ + (distancedX - originX) * Math.sin(calcAngle) + (distancedZ - originZ) * Math.cos(calcAngle);
		return new Vec3(x, original.y, z);
	}

	@Contract(pure = true)
	public static double getAngleFromOriginXZ(@NotNull Vec3 pos) { // https://stackoverflow.com/questions/35271222/getting-the-angle-from-a-direction-vector
		double angleRad = Math.atan2(pos.x, pos.z);
		double degrees = angleRad * Mth.RAD_TO_DEG;
		return (360D + Math.round(degrees)) % 360D;
	}


	public static double getAngleBetweenXZ(@NotNull Vec3 posA, @NotNull Vec3 posB) {
	    double angle = Math.atan2(posA.x - posB.x, posA.z - posB.z);
		return (360D + (angle * Mth.RAD_TO_DEG)) % 360D;
	}
}
