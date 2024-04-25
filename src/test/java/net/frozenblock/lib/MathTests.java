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

package net.frozenblock.lib;

import net.frozenblock.lib.math.api.AdvancedMath;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MathTests {

	@Test
	void testFactorial() {
		// Test factorial of 0
		int n1 = 0;
		int expected1 = 1;
		int actual1 = AdvancedMath.factorial(n1);
		Assertions.assertEquals(expected1, actual1);

		// Test factorial of 1
		int n2 = 1;
		int expected2 = 1;
		int actual2 = AdvancedMath.factorial(n2);
		Assertions.assertEquals(expected2, actual2);

		// Test factorial of 5
		int n3 = 5;
		int expected3 = 120;
		int actual3 = AdvancedMath.factorial(n3);
		Assertions.assertEquals(expected3, actual3);

		// Test factorial of a large number
		int n4 = 12;
		int expected4 = 479001600;
		int actual4 = AdvancedMath.factorial(n4);
		Assertions.assertEquals(expected4, actual4);

		// Test factorial of a negative number
		int n5 = -3;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.factorial(n5);
		});
	}

	@Test
	void testPermutations() {
		// Test valid input
		int n1 = 5;
		int r1 = 3;
		int expected1 = 60;
		int actual1 = AdvancedMath.permutations(n1, r1);
		Assertions.assertEquals(expected1, actual1);

		// Test r > n
		int n2 = 3;
		int r2 = 5;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.permutations(n2, r2);
		});

		// Test n < 0
		int n3 = -2;
		int r3 = 1;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.permutations(n3, r3);
		});

		// Test r < 0
		int n4 = 10;
		int r4 = -3;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.permutations(n4, r4);
		});

		// Test n = 0 and r = 0
		int n5 = 0;
		int r5 = 0;
		int expected5 = 1;
		int actual5 = AdvancedMath.permutations(n5, r5);
		Assertions.assertEquals(expected5, actual5);
	}

	@Test
	void testCombinations() {
		// Test valid input
		int n1 = 5;
		int r1 = 3;
		int expected1 = 10;
		int actual1 = AdvancedMath.combinations(n1, r1);
		Assertions.assertEquals(expected1, actual1);

		// Test r > n
		int n2 = 3;
		int r2 = 5;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.combinations(n2, r2);
		});

		// Test n < 0
		int n3 = -2;
		int r3 = 1;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.combinations(n3, r3);
		});

		// Test r < 0
		int n4 = 10;
		int r4 = -3;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.combinations(n4, r4);
		});

		// Test n = 0 and r = 0
		int n5 = 0;
		int r5 = 0;
		int expected5 = 1;
		int actual5 = AdvancedMath.combinations(n5, r5);
		Assertions.assertEquals(expected5, actual5);
	}

	@Test
	void testQuadraticEquation() {
		// Test a case where two real roots exist
		double[] roots1 = AdvancedMath.solveQuadraticEquation(2, -7, 3);
		Assertions.assertArrayEquals(new double[]{3, 0.5}, roots1, 0.0001);

		// Test a case where one real root exists
		double[] roots2 = AdvancedMath.solveQuadraticEquation(1, -2, 1);
		Assertions.assertArrayEquals(new double[]{1}, roots2, 0.0001);

		// Test a case where no real roots exist
		double[] roots3 = AdvancedMath.solveQuadraticEquation(1, 2, 3);
		Assertions.assertNull(roots3);

		// Test the case where a is zero
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			AdvancedMath.solveQuadraticEquation(0, 2, 3);
		});
	}

	@Test
	void testGreatestCommonDivisor() {
		// Test cases with positive integers
		assertEquals(6, AdvancedMath.greatestCommonDivisor(30, 42));
		assertEquals(1, AdvancedMath.greatestCommonDivisor(17, 23));
		assertEquals(7, AdvancedMath.greatestCommonDivisor(28, 35));
		assertEquals(2, AdvancedMath.greatestCommonDivisor(18, 20));

		// Test cases with negative integers
		assertEquals(6, AdvancedMath.greatestCommonDivisor(-30, -42));
		assertEquals(1, AdvancedMath.greatestCommonDivisor(-17, 23));
		assertEquals(7, AdvancedMath.greatestCommonDivisor(-28, 35));
		assertEquals(2, AdvancedMath.greatestCommonDivisor(18, -20));

		// Test cases with one or both integers equal to zero
		assertEquals(5, AdvancedMath.greatestCommonDivisor(0, 5));
		assertEquals(7, AdvancedMath.greatestCommonDivisor(7, 0));
		assertEquals(0, AdvancedMath.greatestCommonDivisor(0, 0));
	}
}
