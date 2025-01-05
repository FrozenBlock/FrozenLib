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

package net.frozenblock.lib.gravity.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GravityContext {

    public final ResourceKey<Level> dimension;

    public final double y;

    /**
     * A mutable property that will determine the outputting gravity
     */
    public Vec3 gravity;

    @Nullable
    public final Entity entity;

    public GravityContext(ResourceKey<Level> dimension, double y, @Nullable Entity entity) {
        this(dimension, y, GravityAPI.DEFAULT_GRAVITY, entity);
    }

    public GravityContext(ResourceKey<Level> dimension, double y, Vec3 gravity, @Nullable Entity entity) {
        this.dimension = dimension;
        this.y = y;
        this.gravity = gravity;
        this.entity = entity;
    }
}
