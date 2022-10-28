/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl;

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public abstract class SurfaceRuleContextImpl extends ReloadableSequenceMaterialRule implements SurfaceRuleContext {
    SurfaceRules.RuleSource vanillaRules;
    private ResourceManager resourceManager;

    public SurfaceRuleContextImpl(SurfaceRules.RuleSource rules) {
        this.setup(rules);
    }

    private void setup(@NotNull SurfaceRules.RuleSource rules) {
        this.materialRules().clear();
        this.vanillaRules = rules;
        this.materialRules().add(rules);
    }

    @Override
    public @NotNull List<SurfaceRules.RuleSource> materialRules() {
        return this.sequence();
    }

    @Override
    public @NotNull ResourceManager resourceManager() {
        return this.resourceManager;
    }

    void reset(VanillaSurfaceRuleTracker<? extends SurfaceRuleContextImpl> tracker, ResourceManager resourceManager) {
        tracker.pause();
        this.setup(this.getVanillaRules());
        tracker.unpause();
        this.resourceManager = resourceManager;
    }

    void cleanup() {
        // No need to keep references lying around, especially if some are susceptible to being GC-ed.
        this.vanillaRules = null;
        this.resourceManager = null;
    }

    @ApiStatus.OverrideOnly
    protected abstract SurfaceRules.RuleSource getVanillaRules();

    @ApiStatus.Internal
    public static class OverworldImpl extends SurfaceRuleContextImpl implements SurfaceRuleContext.Overworld {
        private final boolean surface;
        private final boolean bedrockRoof;
        private final boolean bedrockFloor;

        public OverworldImpl(boolean surface, boolean bedrockRoof, boolean bedrockFloor, SurfaceRules.RuleSource rules) {
            super(rules);

            this.surface = surface;
            this.bedrockRoof = bedrockRoof;
            this.bedrockFloor = bedrockFloor;
        }

        @Override
        public boolean hasSurface() {
            return this.surface;
        }

        @Override
        public boolean hasBedrockRoof() {
            return this.bedrockRoof;
        }

        @Override
        public boolean hasBedrockFloor() {
            return this.bedrockFloor;
        }

        @Override
        protected SurfaceRules.RuleSource getVanillaRules() {
            return SurfaceRuleData.overworldLike(this.surface, this.bedrockRoof, this.bedrockFloor);
        }
    }

    @ApiStatus.Internal
    public static class NetherImpl extends SurfaceRuleContextImpl implements SurfaceRuleContext.Nether {
        public NetherImpl(SurfaceRules.RuleSource rules) {
            super(rules);
        }

        @Override
        protected SurfaceRules.RuleSource getVanillaRules() {
            return SurfaceRuleData.nether();
        }
    }

    @ApiStatus.Internal
    public static class TheEndImpl extends SurfaceRuleContextImpl implements SurfaceRuleContext.TheEnd {
        public TheEndImpl(SurfaceRules.RuleSource rules) {
            super(rules);
        }

        @Override
        protected SurfaceRules.RuleSource getVanillaRules() {
            return SurfaceRuleData.end();
        }
    }
}
