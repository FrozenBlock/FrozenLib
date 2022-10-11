package net.frozenblock.lib.mathematics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Adds more math operations.
 * <p>
 * Only for FrozenBlock Modders, ALL RIGHTS RESERVED
 *
 * @author LiukRast (2021-2022)
 * @since 4.0
 */
public final class AdvancedMath {

    public static float range(final float min, final float max,
                              final float number) {
        return (number * max) + min;
    }

    public static double randomPosNeg() {
        return Math.random() * (Math.random() >= 0.5 ? 1 : -1);
    }


    public static boolean squareBetween(final int x, final int z,
                                        final int between1,
                                        final int between2) {
        boolean cond1 = x > between1 && x < between2;
        boolean cond2 = z > between1 && z < between2;
        return cond1 && cond2;
    }

    public static BlockPos offset(final BlockPos pos, final Direction dir,
                                  final int a) {
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

    public static double cutCos(final double value, final double offset,
                                final boolean inverse) {
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
        if (axis == Direction.Axis.Y) {
            if (random > 0.5) {
                return Direction.UP;
            } else {
                return Direction.DOWN;
            }
        } else if (axis == Direction.Axis.X) {
            if (random > 0.5) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else {
            if (random > 0.5) {
                return Direction.NORTH;
            } else {
                return Direction.SOUTH;
            }
        }
    }

    private AdvancedMath() {
    }
}
