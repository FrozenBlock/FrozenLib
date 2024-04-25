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

import java.io.Serializable;

/**
 * Same as Java's Point2D, but with 3D coordinates.
 * <p>
 * Allows you to create a 3D point with x, y and z.
 *
 * @author LiukRast (2021-2022)
 * @since 4.0
 */
public abstract class Point3D implements Cloneable {

	/**
	 * The {@code Float} class defines a point in 3D space specified in float
	 * precision.
	 */
	public static class Float extends Point3D implements Serializable {
		public float x;
		public float y;
		public float z;

		public Float() {
		}

		public Float(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}


		public void setLocation(double x, double y, double z) {
			this.x = (float) x;
			this.y = (float) y;
			this.z = (float) z;
		}

		public void setLocation(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String toString() {
			return "Point3D.Float[" + x + ", " + y + ", " + z + "]";
		}
	}

	public static class Double extends Point3D implements Serializable {

		public double x;
		public double y;
		public double z;


		public Double() {
		}

		public Double(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return y;
		}

		public void setLocation(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String toString() {
			return "Point3D.Double[" + x + ", " + y + ", " + z + "]";
		}
	}

	protected Point3D() {
	}

	public abstract double getX();

	public abstract double getY();

	public abstract double getZ();


	public abstract void setLocation(double x, double y, double z);

	public void setLocation(Point3D p) {
		setLocation(p.getX(), p.getY(), p.getZ());
	}

	public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
		x1 -= x2;
		y1 -= y2;
		z1 -= z2;
		return (x1 * x1 + y1 * y1 + z1 * z1);
	}

	public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		x1 -= x2;
		y1 -= y2;
		z1 -= z2;
		return Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
	}

	public double distanceSq(double px, double py, double pz) {
		px -= getX();
		py -= getY();
		pz -= getZ();
		return (px * px + py * py + pz * pz);
	}

	public double distanceSq(Point3D pt) {
		double px = pt.getX() - this.getX();
		double py = pt.getY() - this.getY();
		double pz = pt.getZ() - this.getZ();
		return (px * px + py * py + pz * pz);
	}

	public double distance(double px, double py, double pz) {
		px -= getX();
		py -= getY();
		pz -= getZ();
		return Math.sqrt(px * px + py * py + pz * pz);
	}


	public double distance(Point3D pt) {
		double px = pt.getX() - this.getX();
		double py = pt.getY() - this.getY();
		double pz = pt.getZ() - this.getZ();
		return Math.sqrt(px * px + py * py + pz * pz);
	}


	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError(e);
		}
	}


	public int hashCode() {
		long bits = java.lang.Double.doubleToLongBits(getX());
		bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}


	public boolean equals(Object obj) {
		if (obj instanceof Point3D p2d) {
			return (getX() == p2d.getX()) && (getY() == p2d.getY()) && (getZ() == p2d.getZ());
		}
		return super.equals(obj);
	}
}

