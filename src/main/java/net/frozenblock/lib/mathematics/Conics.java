package net.frozenblock.lib.mathematics;

import java.awt.geom.Point2D;

/**
 * CONICS
 * <p>
 * Allows defining conics via equations
 * <p>
 * Only for FrozenBlock Modders, ALL RIGHTS RESERVED
 * <p>
 * Defining a point or 3D point allows you to define if it is inside or at the border of a conic
 *
 * @author LiukRast (2021-2022)
 * @since 4.0
 */
public class Conics {
    public static boolean isCircle(Point2D center, float radius,
                                   Point2D actual) {
        float curvex = (float) Math.pow(actual.getX() - center.getX(), 2);
        float curvey = (float) Math.pow(actual.getY() - center.getY(), 2);
        return curvex + curvey == (float) Math.pow(radius, 2);
    }

    public static boolean isInsideCircle(Point2D center, float radius,
                                         Point2D actual) {
        float curvex = (float) Math.pow(actual.getX() - center.getX(), 2);
        float curvey = (float) Math.pow(actual.getY() - center.getY(), 2);
        return curvex + curvey <= (float) Math.pow(radius, 2);
    }

    public static boolean isEllipsoid(Point3D center, float a, float b, float c,
                                      Point3D actual) {
        float curvex =
                (float) ((float) (Math.pow(actual.getX() - center.getX(), 2)) /
                        (Math.pow(a, 2)));
        float curvey =
                (float) ((float) (Math.pow(actual.getY() - center.getY(), 2)) /
                        (Math.pow(b, 2)));
        float curvez =
                (float) ((float) (Math.pow(actual.getZ() - center.getZ(), 2)) /
                        (Math.pow(c, 2)));
        return curvex + curvey + curvez == 1;
    }

    public static boolean isInsideEllipsoid(Point3D center, float a, float b,
                                            float c, Point3D actual) {
        float curvex =
                (float) ((float) (Math.pow(actual.getX() - center.getX(), 2)) /
                        (Math.pow(a, 2)));
        float curvey =
                (float) ((float) (Math.pow(actual.getY() - center.getY(), 2)) /
                        (Math.pow(b, 2)));
        float curvez =
                (float) ((float) (Math.pow(actual.getZ() - center.getZ(), 2)) /
                        (Math.pow(c, 2)));
        return curvex + curvey + curvez <= 1;
    }
}
