/*
 * Copyright 2022 QuiltMC
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
 */

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl;

import net.frozenblock.lib.worldgen.surface.FrozenSurfaceRules;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceLocation;
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
public class SurfaceRuleContextImpl implements SurfaceRuleContext {
    private final SurfaceRules.SequenceRuleSource ruleSource;
    private final ResourceManager resourceManager;
	private final ResourceLocation resourceLocation;

    public SurfaceRuleContextImpl(SurfaceRules.RuleSource rules, ResourceManager resourceManager, ResourceLocation resourceLocation) {
        this.ruleSource = FrozenSurfaceRules.sequence(new ArrayList<>());
		this.ruleSources().add(rules);
		this.resourceManager = resourceManager;
		this.resourceLocation = resourceLocation;
    }

	public SurfaceRules.RuleSource freeze() {
		((QuiltSequenceMaterialRuleHooks) (Object) this.ruleSource).frozenblock_quilt$freeze();
		return this.ruleSource;
	}

    @Override
    public @NotNull List<SurfaceRules.RuleSource> ruleSources() {
        return this.ruleSource.sequence();
    }

    @Override
    public @NotNull ResourceManager resourceManager() {
        return this.resourceManager;
    }

	@Override
	public @NotNull ResourceLocation resourceLocation() {
		return this.resourceLocation;
	}

    @ApiStatus.Internal
    public static class OverworldImpl extends SurfaceRuleContextImpl implements SurfaceRuleContext.Overworld {
        private final boolean surface;
        private final boolean bedrockRoof;
        private final boolean bedrockFloor;

        public OverworldImpl(boolean surface, boolean bedrockRoof, boolean bedrockFloor,
				SurfaceRules.RuleSource rules, ResourceManager resourceManager, ResourceLocation resourceLocation) {
            super(rules, resourceManager, resourceLocation);

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
    }

    @ApiStatus.Internal
    public static class NetherImpl extends SurfaceRuleContextImpl implements SurfaceRuleContext.Nether {
        public NetherImpl(SurfaceRules.RuleSource rules, ResourceManager resourceManager, ResourceLocation resourceLocation) {
            super(rules, resourceManager, resourceLocation);
        }
    }

    @ApiStatus.Internal
    public static class TheEndImpl extends SurfaceRuleContextImpl implements SurfaceRuleContext.TheEnd {
        public TheEndImpl(SurfaceRules.RuleSource rules, ResourceManager resourceManager, ResourceLocation resourceLocation) {
            super(rules, resourceManager, resourceLocation);
        }
    }
}
