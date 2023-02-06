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
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class FrozenTerraBlenderCompat implements TerraBlenderApi {

	@Override
	public void onTerraBlenderInitialized() {
		//OVERWORLD
		ArrayList<SurfaceRules.RuleSource> overworldRules = new ArrayList<>();
		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addRuleSources(overworldRules);
		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRules(overworldRules)));

		SurfaceRules.RuleSource overworldSource = null;
		for (SurfaceRules.RuleSource ruleSource : overworldRules) {
			if (overworldSource == null) {
				overworldSource = ruleSource;
			} else {
				overworldSource = SurfaceRules.sequence(overworldSource, ruleSource);
			}
		}

		ArrayList<SurfaceRules.RuleSource> overworldNoPrelimRules = new ArrayList<>();
		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE.invoker().addRuleSources(overworldNoPrelimRules);
		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRulesNoPrelimSurface(overworldNoPrelimRules)));

		SurfaceRules.RuleSource overworldNoPrelimSource = null;
		for (SurfaceRules.RuleSource ruleSource : overworldNoPrelimRules) {
			if (overworldNoPrelimSource == null) {
				overworldNoPrelimSource = ruleSource;
			} else {
				overworldNoPrelimSource = SurfaceRules.sequence(overworldNoPrelimSource, ruleSource);
			}
		}

		if (overworldSource != null) {
			overworldSource = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), overworldSource);
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, overworldSource);
		}

		if (overworldNoPrelimSource != null) {
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, overworldNoPrelimSource);
		}

		//NETHER
		ArrayList<SurfaceRules.RuleSource> netherRules = new ArrayList<>();
		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_NETHER.invoker().addRuleSources(netherRules);
		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addNetherSurfaceRules(netherRules)));

		SurfaceRules.RuleSource netherSource = null;
		for (SurfaceRules.RuleSource ruleSource : netherRules) {
			if (netherSource == null) {
				netherSource = ruleSource;
			} else {
				netherSource = SurfaceRules.sequence(netherSource, ruleSource);
			}
		}

		if (netherSource != null) {
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.NETHER, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, netherSource);
		}
	}

}
