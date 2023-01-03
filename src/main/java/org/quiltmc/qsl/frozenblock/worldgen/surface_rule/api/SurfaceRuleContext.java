/*
 * Copyright 2023 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a context about surface rules for ease of modification of them.
 *
 * @see SurfaceRuleEvents
 * <p>
 * Modified to work on Fabric
 */
public interface SurfaceRuleContext {
    /**
     * {@return the list of the current surface material rules present}
     * <p>
     * The list is mutable.
     */
    @Contract(pure = true)
    @NotNull List<SurfaceRules.RuleSource> materialRules();

    /**
     * {@return the resource manager of the current set of loaded data-packs}
     */
    @Contract(pure = true)
    @NotNull ResourceManager resourceManager();

    /**
     * Represents the Overworld-specific context.
     */
    interface Overworld extends SurfaceRuleContext {
        /**
         * {@return {@code true} if this overworld dimension should have a surface exposed to the sky, or {@code false} otherwise}
         */
        @Contract(pure = true)
        boolean hasSurface();

        /**
         * {@return {@code true} if this overworld dimension should have a bedrock roof, or {@code false} otherwise}
         */
        @Contract(pure = true)
        boolean hasBedrockRoof();

        /**
         * {@return {@code true} if this overworld dimension should have a bedrock floor, or {@code false} otherwise}
         */
        @Contract(pure = true)
        boolean hasBedrockFloor();
    }

    /**
     * Represents the Nether-specific context.
     */
    interface Nether extends SurfaceRuleContext {
    }

    /**
     * Represents the End-specific context.
     */
    interface TheEnd extends SurfaceRuleContext {
    }
}
