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

package net.frozenblock.terrablender.mixin;

import java.util.ArrayList;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrablender.worldgen.TBSurfaceRuleData;

@Mixin(TBSurfaceRuleData.class)
public class TBSurfaceRuleDataMixin {

	@ModifyVariable(method = "overworldLike", at = @At("STORE"), ordinal = 8)
	private static SurfaceRules.RuleSource addNewOverworldRules(SurfaceRules.RuleSource source, boolean abovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor) {
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addRuleSources(sourceHolders);

		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRules(sourceHolders)));

		SurfaceRules.RuleSource newSource = null;
		for (SurfaceRules.RuleSource ruleSource : sourceHolders) {
			if (newSource == null) {
				newSource = ruleSource;
			} else {
				newSource = SurfaceRules.sequence(newSource, ruleSource);
			}
		}

		if (newSource != null) {
			return SurfaceRules.sequence(newSource, source, newSource);
		}
		return source;
	}

	@ModifyVariable(method = "nether", at = @At("STORE"), ordinal = 2)
	private static SurfaceRules.RuleSource addNewNetherRules(SurfaceRules.RuleSource source) {
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_NETHER.invoker().addRuleSources(sourceHolders);

		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addNetherSurfaceRules(sourceHolders)));

		SurfaceRules.RuleSource newSource = null;
		for (SurfaceRules.RuleSource ruleSource : sourceHolders) {
			if (newSource == null) {
				newSource = ruleSource;
			} else {
				newSource = SurfaceRules.sequence(newSource, ruleSource);
			}
		}

		if (newSource != null) {
			return SurfaceRules.sequence(newSource, source, newSource);
		}
		return source;
	}

}
