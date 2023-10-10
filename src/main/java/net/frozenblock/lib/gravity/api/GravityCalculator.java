package net.frozenblock.lib.gravity.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public final class GravityCalculator {
    private GravityCalculator() {}

    private static final Map<ResourceKey<DimensionType>, List<GravityBelt>> GRAVITY_BELTS = new HashMap<>();

    public static void register(ResourceKey<DimensionType> dimension, GravityBelt gravityBelt) {
        if (!GRAVITY_BELTS.containsKey(dimension)) GRAVITY_BELTS.put(dimension, new ArrayList<>());
        GRAVITY_BELTS.get(dimension).add(gravityBelt);
    }

    @Nullable
    public static List<GravityBelt> getAllBelts(ResourceKey<DimensionType> dimension) {
        return GRAVITY_BELTS.get(dimension);
    }

    public static List<GravityBelt> getAllBelts(Level level) {
        return getAllBelts(level.dimensionTypeId());
    }

    public static double calculateGravity(ResourceKey<DimensionType> dimension, double y) {
        if (GRAVITY_BELTS.containsKey(dimension)) {
            Optional<GravityBelt> optionalGravityBelt = getAffectingGravityBelt(GRAVITY_BELTS.get(dimension), y);
            if (optionalGravityBelt.isPresent()) {
                GravityBelt belt = optionalGravityBelt.get();
                return belt.getGravity(null, y);
            }
        }
        return 1.0;
    }

    public static double calculateGravity(Level level, double y) {
        return calculateGravity(level.dimensionTypeId(), y);
    }

    public static double calculateGravity(Entity entity) {
        ResourceKey<DimensionType> dimension = entity.level().dimensionTypeId();
        if (GRAVITY_BELTS.containsKey(dimension)) {
            double y = entity.getY();
            Optional<GravityBelt> optionalGravityBelt = getAffectingGravityBelt(GRAVITY_BELTS.get(dimension), y);
            if (optionalGravityBelt.isPresent()) {
                GravityBelt belt = optionalGravityBelt.get();
                double gravity = belt.getGravity(entity, y);
                // at some point add extensions or something
                return gravity;
            }
        }
        return 1.0;
    }

    public static Direction getGravityDirection(Entity entity) {
        return calculateGravity(entity) >= 0 ? Direction.DOWN : Direction.UP;
    }

    public static boolean isGravityDown(Entity entity) {
        return getGravityDirection(entity) == Direction.DOWN;
    }

    public static Optional<GravityBelt> getAffectingGravityBelt(List<GravityBelt> belts, double y) {
        Optional<GravityBelt> optionalGravityBelt = Optional.empty();
        for (GravityBelt belt : belts) {
            if (belt.affectsPosition(y)) {
                optionalGravityBelt = Optional.of(belt);
                break;
            }
        }
        return optionalGravityBelt;
    }

    public record GravityBelt(double minY, double maxY, boolean renderBottom, boolean renderTop, GravityFunction function) {

        public boolean affectsPosition(double y) {
            return y >= minY && y < maxY;
        }

        protected double getGravity(@Nullable Entity entity, double y) {
            if (this.affectsPosition(y)) {
                return this.function.get(entity, y);
            }
            return 1.0;
        }
    }

    @FunctionalInterface
    public interface GravityFunction {
        double get(@Nullable Entity entity, double y);
    }
}