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

package net.frozenblock.lib.worldgen.surface.mixin;

import java.util.ArrayList;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SurfaceRuleData.class)
public class SurfaceRuleDataMixin {

	@Inject(method = "overworldLike", at = @At("RETURN"), cancellable = true)
	private static void frozenlib$injectOverworldLikeRules(boolean abovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor, CallbackInfoReturnable<SurfaceRules.RuleSource> info) {
		ArrayList<SurfaceRules.RuleSource> sources = new ArrayList<>();

		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addRuleSources(sources);
		SurfaceRuleEvents.MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE.invoker().addRuleSources(sources);

		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRules(sources)));
		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRulesNoPrelimSurface(sources)));

		frozenLib$addRuleSources(info, sources);
	}

	@Inject(method = "nether", at = @At("RETURN"), cancellable = true)
	private static void frozenLib$injectNetherRules(CallbackInfoReturnable<SurfaceRules.RuleSource> info) {
		ArrayList<SurfaceRules.RuleSource> sources = new ArrayList<>();

		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_NETHER.invoker().addRuleSources(sources);

		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addNetherSurfaceRules(sources)));

		frozenLib$addRuleSources(info, sources);
	}


	@Inject(method = "end", at = @At("RETURN"), cancellable = true)
	private static void frozenLib$injectEndRules(CallbackInfoReturnable<SurfaceRules.RuleSource> info) {
		ArrayList<SurfaceRules.RuleSource> sources = new ArrayList<>();

		//TODO: Fix i guess idk
		SurfaceRuleEvents.MODIFY_END.invoker().addRuleSources(sources);

		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addEndSurfaceRules(sources)));

		frozenLib$addRuleSources(info, sources);
	}

	@Unique
	private static void frozenLib$addRuleSources(CallbackInfoReturnable<SurfaceRules.RuleSource> info, ArrayList<SurfaceRules.RuleSource> sources) {
		if (!sources.isEmpty()) {
			SurfaceRules.RuleSource ruleSource = null;
			for (SurfaceRules.RuleSource source : sources) {
				if (ruleSource == null) {
					ruleSource = SurfaceRules.sequence(source);
				} else {
					ruleSource = SurfaceRules.sequence(ruleSource, source);
				}
			}
			info.setReturnValue(SurfaceRules.sequence(info.getReturnValue(), ruleSource));
		}
	}

}
