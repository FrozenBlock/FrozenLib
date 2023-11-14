package net.frozenblock.lib.gravity.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;

public class GravityContext {

    public final ResourceKey<DimensionType> dimension;

    public final double y;

    /**
     * A mutable property that will determine the outputting gravity
     */
    public double gravity;

    @Nullable
    public final Entity entity;

    public GravityBelt(ResourceKey<DimensionType> dimension, double y, @Nullable Entity entity) {
        this(dimension, y, 1.0, entity);
    }

    public GravityContext(ResourceKey<DimensionType> dimension, double y, double gravity, @Nullable Entity entity) {
        this.dimension = dimension;
        this.y = y;
        this.gravity = gravity;
        this.entity = entity;
    }
}