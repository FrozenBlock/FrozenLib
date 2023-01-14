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

package net.frozenblock.lib.worldgen.surface.api;

import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * An entrypoint that lets you add surface rules to dimensions.
 * <p>
 * Defined with the {@code frozenlib:surfacerules} key in {@code fabric.mod.json}.
 * Compatible with TerraBlender.
 */
public interface FrozenSurfaceRuleEntrypoint {

	/**
	 * Adds all {@link SurfaceRules.RuleSource}s added to the list (context) to Overworld world presets.
	 */
	void addOverworldSurfaceRules(ArrayList<SurfaceRules.RuleSource> context);

	/**
	 * Adds all {@link SurfaceRules.RuleSource}s added to the list (context) to Overworld world presets without checking the preliminary surface.
	 */
	void addOverworldSurfaceRulesNoPrelimSurface(ArrayList<SurfaceRules.RuleSource> context);

	/**
	 * Adds all {@link SurfaceRules.RuleSource}s added to the list (context) to Nether world presets.
	 */
	void addNetherSurfaceRules(ArrayList<SurfaceRules.RuleSource> context);

	/**
	 * Adds all {@link SurfaceRules.RuleSource}s added to the list (context) to End world presets.
	 */
	void addEndSurfaceRules(ArrayList<SurfaceRules.RuleSource> context);

	/**
	 * Adds all surface rules added to the list (context) to and specified world preset.
	 * <p>
	 * Context holds {@link FrozenDimensionBoundRuleSource}s, which hold both a {@link SurfaceRules.RuleSource} and a {@link ResourceLocation}.
	 * <p>
	 * The {@link ResourceLocation} specifies which world preset to add the {@link SurfaceRules.RuleSource}s to.
	 * <p>
	 * <p>
	 * Example given:
	 * <p>
	 * {@code context.add(new FrozenDimensionBoundRuleSource(new ResourceLocation("minecraft", "amplified"), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.Desert), FrozenSurfaceRules.makeStateRule(Blocks.BEDROCK))));}
	 */
	void addSurfaceRules(ArrayList<FrozenDimensionBoundRuleSource> context);

}
