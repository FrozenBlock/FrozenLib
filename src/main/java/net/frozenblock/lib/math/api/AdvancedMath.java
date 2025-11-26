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
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class AdvancedMath {

	@Contract(" -> new")
	public static RandomSource random() {
		return RandomSource.create();
	}

    public static float range(final float min, final float max, final float number) {
        return (number * max) + min;
    }

    public static double randomPosNeg() {
		final RandomSource random = random();
        return random.nextDouble() * (random.nextBoolean() ? 1D : -1D);
    }

    public static boolean squareBetween(final int x, final int z, final int between1, final int between2) {
		final boolean cond1 = x > between1 && x < between2;
		final boolean cond2 = z > between1 && z < between2;
        return cond1 && cond2;
    }

    public static double cutCos(double value, double offset, boolean inverse) {
		final double equation = Math.cos(value);
        if (!inverse) return Math.max(equation, offset);
		return Math.max(-equation, offset);
    }

	public static int factorial(int n) {
		if (n < 0) throw new IllegalArgumentException("Factorial of negative numbers is undefined");

		int result = 1;
		for (int i = 2; i <= n; i++) result *= i;

		return result;
	}

	public static int permutations(int n, int r) {
		if (n < 0 || r < 0 || r > n) throw new IllegalArgumentException("Invalid input: n must be non-negative, r must be non-negative and not greater than n");

		int result = 1;
		for (int i = n; i > n - r; i--) result *= i;

		return result;
	}

	public static int combinations(int n, int r) {
		if (n < 0 || r < 0 || r > n) throw new IllegalArgumentException("Invalid input: n must be non-negative, r must be non-negative and not greater than n");

		int numerator = 1;
		for (int i = n; i > n - r; i--) numerator *= i;

		int denominator = 1;
		for (int i = r; i > 0; i--) denominator *= i;

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
		if (a == 0) throw new IllegalArgumentException("a cannot be zero");

		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) return null;

		if (discriminant == 0) {
			final double root = -b / (2 * a);
			return new double[]{root};
		}

		final double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
		final double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
		return new double[]{root1, root2};
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
    public static Direction randomDir(final Direction.Axis axis) {
        final boolean random = random().nextBoolean();
		switch (axis) {
			case X -> {
				return random ? Direction.EAST : Direction.WEST;
			}
			case Y -> {
				return random ? Direction.UP : Direction.DOWN;
			}
			default -> {
				return random ? Direction.NORTH : Direction.SOUTH;
			}
		}
    }

	public static double distanceBetween(BlockPos center, BlockPos currentPos, boolean includeY) {
		final double xSquared = Mth.square(center.getX() - currentPos.getX());
		final double ySquared = includeY ? Mth.square(center.getY() - currentPos.getY()) : 0D;
		final double zSquared = Mth.square(center.getZ() - currentPos.getZ());
		return Math.sqrt(xSquared + ySquared + zSquared);
	}

	public static Vec3 rotateAboutXZ(Vec3 original, double distanceFrom, double angle) {
		final double calcAngle = angle * (Math.PI / 180D);
		final Vec3 offsetVec = original.add(distanceFrom, 0, distanceFrom);

		final double originX = original.x;
		final double originZ = original.z;
		final double distancedX = offsetVec.x;
		final double distancedZ = offsetVec.z;

		final double x = originX + (distancedX - originX) * Math.cos(calcAngle) - (distancedZ - originZ) * Math.sin(calcAngle);
		final double z = originZ + (distancedX - originX) * Math.sin(calcAngle) + (distancedZ - originZ) * Math.cos(calcAngle);
		return new Vec3(x, original.y, z);
	}

	public static Vec3 rotateAboutX(Vec3 original, double distanceFrom, double angle) {
		final double calcAngle = angle * (Math.PI / 180D);
		final Vec3 offsetVec = original.add(distanceFrom, 0, 0);

		final double originX = original.x;
		final double originZ = original.z;
		final double distancedX = offsetVec.x;
		final double distancedZ = offsetVec.z;

		final double x = originX + (distancedX - originX) * Math.cos(calcAngle) - (distancedZ - originZ) * Math.sin(calcAngle);
		final double z = originZ + (distancedX - originX) * Math.sin(calcAngle) + (distancedZ - originZ) * Math.cos(calcAngle);
		return new Vec3(x, original.y, z);
	}

	@Contract(pure = true)
	public static double getAngleFromOriginXZ(Vec3 pos) { // https://stackoverflow.com/questions/35271222/getting-the-angle-from-a-direction-vector
		final double angleRad = Math.atan2(pos.x, pos.z);
		final double degrees = angleRad * Mth.RAD_TO_DEG;
		return (360D + Math.round(degrees)) % 360D;
	}

	public static double getAngleBetweenXZ(Vec3 posA, Vec3 posB) {
		final double angle = Math.atan2(posA.x - posB.x, posA.z - posB.z);
		return (360D + (angle * Mth.RAD_TO_DEG)) % 360D;
	}
}
