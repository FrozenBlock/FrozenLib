package net.frozenblock.api.mathematics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class AdvancedMath {
    /**
     * ADVANCED MATH
     * <p>
     * Adds more math operations
     * <p>
     * Only for FrozenBlock Modders, ALL RIGHTS RESERVED
     * <p>
     *
     * @author LiukRast (2021-2022)
     * @since 4.0
     */
    public static float range(float min, float max, float number) {
        return (number * max) + min;
    }

    public static double randomPosNeg() {
        return Math.random() * (Math.random() >= 0.5 ? 1 : -1);
    }



    public static boolean squareBetween(int x, int z, int between1, int between2) {
        boolean cond1 = x > between1 && x < between2;
        boolean cond2 = z > between1 && z < between2;
        return cond1 && cond2;
    }

    public static BlockPos offset(BlockPos pos, Direction dir, int a) {
        return switch(dir) {
            case WEST -> pos.west(a);
            case EAST -> pos.east(a);
            case SOUTH -> pos.south(a);
            case NORTH -> pos.north(a);
            case UP -> pos.above(a);
            case DOWN -> pos.below(a);
        };
    }

    public static BlockPos offset(BlockPos pos, Direction dir) {
        return offset(pos, dir, 1);
    }

    public static int waterToHollowedProperty(int value) {
        if (value > 8) {
            return 8;
        } else if (value < 0) {
            return -1;
        } else {
            return value;
        }
    }

    public static int waterLevelReduce(int value) {
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
}
