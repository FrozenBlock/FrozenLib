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

package net.frozenblock.lib.math.api;

import java.awt.geom.Point2D;
import lombok.experimental.UtilityClass;

/**
 * Allows defining conics via equations.
 * <p>
 * Defining a point or 3D point allows you to define if it is inside or at the border of a conic.
 *
 * @author LiukRast (2021-2022)
 * @since 4.0
 */
@UtilityClass
public class Conics {

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
