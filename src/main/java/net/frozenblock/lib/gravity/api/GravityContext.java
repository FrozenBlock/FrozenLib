/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.gravity.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
        this(dimension, y, new Vec3(0.0, 1.0, 0.0), entity);
    }

    public GravityContext(ResourceKey<Level> dimension, double y, Vec3 gravity, @Nullable Entity entity) {
        this.dimension = dimension;
        this.y = y;
        this.gravity = gravity;
        this.entity = entity;
    }
}
