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

package net.frozenblock.lib_compat.terrablender;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.biome.api.BiomeParameters;
import net.frozenblock.lib.worldgen.biome.api.parameters.OverworldBiomeBuilderParameters;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.ParameterUtils;
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

	public static List<Climate.ParameterPoint> points(BiomeParameters parameters) {
		ParameterUtils.ParameterPointListBuilder builder = new ParameterUtils.ParameterPointListBuilder();
		for (Climate.Parameter parameter : parameters.temperatures) {
			builder.temperature(parameter);
		}
		for (Climate.Parameter parameter : parameters.humidities) {
			builder.humidity(parameter);
		}
		for (Climate.Parameter parameter : parameters.continentalnesses) {
			builder.continentalness(parameter);
		}
		for (Climate.Parameter parameter : parameters.erosions) {
			builder.erosion(parameter);
		}
		for (Climate.Parameter parameter : parameters.depths) {
			builder.depth(parameter);
		}
		for (Climate.Parameter parameter : parameters.weirdnesses) {
			builder.weirdness(parameter);
		}
		for (float f : parameters.offsets) {
			builder.offset(f);
		}
		return builder.build();
	}

	public static List<Climate.ParameterPoint> points(ResourceKey<Biome> key) {
		points(key.location());
	}

	public static List<Climate.ParameterPoint> points(ResourceLocation location) {
		return points(OverworldBiomeBuilderParameters.getParameters(location));
	}

}
