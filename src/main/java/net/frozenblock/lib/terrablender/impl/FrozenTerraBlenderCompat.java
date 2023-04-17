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

package net.frozenblock.lib.terrablender.impl;

import java.util.ArrayList;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRuleEntrypoint;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class FrozenTerraBlenderCompat implements TerraBlenderApi {

	@Override
	public void onTerraBlenderInitialized() {
		//GENERIC
		ArrayList<FrozenDimensionBoundRuleSource> genericSources = new ArrayList<>();
		ArrayList<SurfaceRules.RuleSource> overworldSources = new ArrayList<>();
		ArrayList<SurfaceRules.RuleSource> overworldNoPrelimSources = new ArrayList<>();
		ArrayList<SurfaceRules.RuleSource> netherRules = new ArrayList<>();

		for (EntrypointContainer<FrozenSurfaceRuleEntrypoint> entrypoint : FrozenMain.SURFACE_RULE_ENTRYPOINTS) {
			FrozenSurfaceRuleEntrypoint surfaceRuleEntrypoint = entrypoint.getEntrypoint();
			surfaceRuleEntrypoint.addSurfaceRules(genericSources);
			surfaceRuleEntrypoint.addOverworldSurfaceRules(overworldSources);
			surfaceRuleEntrypoint.addOverworldSurfaceRulesNoPrelimSurface(overworldNoPrelimSources);
			surfaceRuleEntrypoint.addNetherSurfaceRules(netherRules);
		}

		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_GENERIC.invoker().addRuleSources(genericSources);
		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addRuleSources(overworldSources);
		SurfaceRuleEvents.MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE.invoker().addRuleSources(overworldNoPrelimSources);
		SurfaceRuleEvents.MODIFY_NETHER.invoker().addRuleSources(netherRules);

		//OVERWORLD
		for (SurfaceRules.RuleSource ruleSource : overworldSources) {
			FrozenMain.log("added new rule", FrozenMain.UNSTABLE_LOGGING);
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 10, ruleSource);
		}

		//OVERWORLD WITHOUT PRELIMINARY SURFACE
		//TODO: Fix i guess idk
		for (SurfaceRules.RuleSource ruleSource : overworldNoPrelimSources) {
			FrozenMain.log("added new rule", FrozenMain.UNSTABLE_LOGGING);
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.AFTER_BEDROCK, 10, ruleSource);
		}

		//OVERWORLD GENERIC
		for (FrozenDimensionBoundRuleSource dimensionBoundRuleSource : genericSources) {
			if (dimensionBoundRuleSource.dimension().equals((BuiltinDimensionTypes.OVERWORLD.location())) || dimensionBoundRuleSource.dimension().equals((BuiltinDimensionTypes.OVERWORLD_CAVES.location()))) {
				FrozenMain.log("added new rule", FrozenMain.UNSTABLE_LOGGING);
				SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.AFTER_BEDROCK, 10, dimensionBoundRuleSource.ruleSource());
			}
		}

		//NETHER
		//TODO: Fix i guess idk
		for (SurfaceRules.RuleSource ruleSource : netherRules) {
			FrozenMain.log("added new rule", FrozenMain.UNSTABLE_LOGGING);
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.NETHER, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 10, ruleSource);
		}

		//NETHER GENERIC
		for (FrozenDimensionBoundRuleSource dimensionBoundRuleSource : genericSources) {
			if (dimensionBoundRuleSource.dimension().equals((BuiltinDimensionTypes.NETHER.location()))) {
				FrozenMain.log("added new rule", FrozenMain.UNSTABLE_LOGGING);
				SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.NETHER, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 10, dimensionBoundRuleSource.ruleSource());
			}
		}
	}

}
