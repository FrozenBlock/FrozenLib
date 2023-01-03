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
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurfaceRuleData.class)
public class SurfaceRuleDataMixin {

	@ModifyVariable(method = "overworldLike", at = @At("STORE"), ordinal = 8)
	private static SurfaceRules.RuleSource addNewOverworldRules(SurfaceRules.RuleSource source, boolean abovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor) {
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addRuleSources(sourceHolders);

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

	@ModifyVariable(method = "nether", at = @At("RETURN"))
	private static SurfaceRules.RuleSource addNewNetherRules(SurfaceRules.RuleSource source) {
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_NETHER.invoker().addRuleSources(sourceHolders);

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

	@Inject(method = "end", at = @At("RETURN"), cancellable = true)
	private static void addNewEndRules(CallbackInfoReturnable<SurfaceRules.RuleSource> info) {
		ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

		SurfaceRuleEvents.MODIFY_END.invoker().addRuleSources(sourceHolders);

		SurfaceRules.RuleSource newSource = null;
		for (SurfaceRules.RuleSource ruleSource : sourceHolders) {
			if (newSource == null) {
				newSource = ruleSource;
			} else {
				newSource = SurfaceRules.sequence(newSource, ruleSource);
			}
		}

		if (newSource != null) {
			info.setReturnValue(SurfaceRules.sequence(newSource, info.getReturnValue(), newSource));
		}
	}
}

