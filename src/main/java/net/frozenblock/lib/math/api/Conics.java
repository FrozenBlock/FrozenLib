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

package net.frozenblock.lib.math.api;

import java.awt.geom.Point2D;

/**
 * Allows defining conics via equations.
 * <p>
 * Defining a point or 3D point allows you to define if it is inside or at the border of a conic.
 *
 * @author LiukRast (2021-2022)
 * @since 4.0
 */
public final class Conics {

	private Conics() {
		throw new UnsupportedOperationException("Conics contains only static declarations.");
	}

	public static boolean isCircle(Point2D center, double radius, Point2D actual) {
		double curvex = Math.pow(actual.getX() - center.getX(), 2);
		double curvey = Math.pow(actual.getY() - center.getY(), 2);
		return curvex + curvey == Math.pow(radius, 2);
	}

	public static boolean isInsideCircle(Point2D center, double radius, Point2D actual) {
		double curvex = Math.pow(actual.getX() - center.getX(), 2);
		double curvey = Math.pow(actual.getY() - center.getY(), 2);
		return curvex + curvey <= Math.pow(radius, 2);
	}

	public static boolean isEllipsoid(Point3D center, double a, double b, double c, Point3D actual) {
		double curvex = Math.pow(actual.getX() - center.getX(), 2) / Math.pow(a, 2);
		double curvey = Math.pow(actual.getY() - center.getY(), 2) / Math.pow(b, 2);
		double curvez = Math.pow(actual.getZ() - center.getZ(), 2) / Math.pow(c, 2);
		return curvex + curvey + curvez == 1;
	}

	public static boolean isInsideEllipsoid(Point3D center, double a, double b, double c, Point3D actual) {
		double curvex = Math.pow(actual.getX() - center.getX(), 2) / (Math.pow(a, 2));
		double curvey = Math.pow(actual.getY() - center.getY(), 2) / (Math.pow(b, 2));
		double curvez = Math.pow(actual.getZ() - center.getZ(), 2) / (Math.pow(c, 2));
		return curvex + curvey + curvez <= 1;
	}
}
