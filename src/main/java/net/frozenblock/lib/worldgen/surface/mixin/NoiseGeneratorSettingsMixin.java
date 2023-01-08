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
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.FrozenDimensionBoundRuleSource;
import net.frozenblock.lib.worldgen.surface.api.SurfaceRuleEvents;
import net.frozenblock.lib.worldgen.surface.impl.NoiseGeneratorInterface;
import net.minecraft.core.Holder;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorInterface {

	@Shadow
	@Final
	@Mutable
	private SurfaceRules.RuleSource surfaceRule;

	@Unique
	private Holder<DimensionType> frozenLib$dimension;
	@Unique
	private boolean frozenLib$hasCheckedOverworldEntrypoints;
	@Unique
	private boolean frozenLib$hasCheckedNetherEntrypoints;
	@Unique
	private boolean frozenLib$hasCheckedEndEntrypoints;
	@Unique
	private boolean frozenLib$hasCheckedGenericEntrypoints;

	@Inject(method = "surfaceRule", at = @At("HEAD"))
	private void overworld(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		if (!(this.frozenLib$dimension.is(BuiltinDimensionTypes.OVERWORLD) || this.frozenLib$dimension.is(BuiltinDimensionTypes.OVERWORLD_CAVES))) {
			this.frozenLib$hasCheckedOverworldEntrypoints = true;
		}
		if (!this.frozenLib$hasCheckedOverworldEntrypoints && !FrozenBools.HAS_TERRABLENDER) {
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
			this.frozenLib$hasCheckedOverworldEntrypoints = true;

			if (newSource != null) {
				this.surfaceRule = SurfaceRules.sequence(newSource, this.surfaceRule, newSource);
			}
		}
	}

	@Inject(method = "surfaceRule", at = @At("HEAD"))
	private void nether(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		if (!this.frozenLib$dimension.is(BuiltinDimensionTypes.NETHER)) {
			this.frozenLib$hasCheckedNetherEntrypoints = true;
		}
		if (!this.frozenLib$hasCheckedNetherEntrypoints && !FrozenBools.HAS_TERRABLENDER) {
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
			this.frozenLib$hasCheckedNetherEntrypoints = true;

			if (newSource != null) {
				this.surfaceRule = SurfaceRules.sequence(newSource, this.surfaceRule, newSource);
			}
		}
	}

	@Inject(method = "surfaceRule", at = @At("HEAD"))
	private void end(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		if (!this.frozenLib$dimension.is(BuiltinDimensionTypes.END)) {
			this.frozenLib$hasCheckedEndEntrypoints = true;
		}
		if (!this.frozenLib$hasCheckedEndEntrypoints) {
			ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

			//TODO: Fix i guess idk
			SurfaceRuleEvents.MODIFY_NETHER.invoker().addRuleSources(sourceHolders);

			FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addEndSurfaceRules(sourceHolders)));

			SurfaceRules.RuleSource newSource = null;
			for (SurfaceRules.RuleSource ruleSource : sourceHolders) {
				if (newSource == null) {
					newSource = ruleSource;
				} else {
					newSource = SurfaceRules.sequence(newSource, ruleSource);
				}
			}
			this.frozenLib$hasCheckedEndEntrypoints = true;

			if (newSource != null) {
				this.surfaceRule = SurfaceRules.sequence(newSource, this.surfaceRule, newSource);
			}
		}
	}

	@Inject(method = "surfaceRule", at = @At("HEAD"))
	private void generic(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		if (!this.frozenLib$hasCheckedGenericEntrypoints) {
			ArrayList<FrozenDimensionBoundRuleSource> sourceHolders = new ArrayList<>();

			//TODO: Fix i guess idk
			SurfaceRuleEvents.MODIFY_GENERIC.invoker().addRuleSources(sourceHolders);

			FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addSurfaceRules(sourceHolders)));

			SurfaceRules.RuleSource newSource = null;
			for (FrozenDimensionBoundRuleSource dimRuleSource : sourceHolders) {
				if (this.frozenLib$dimension.is(dimRuleSource.dimension)) {
					if (newSource == null) {
						newSource = dimRuleSource.ruleSource;
					} else {
						newSource = SurfaceRules.sequence(newSource, dimRuleSource.ruleSource);
					}
				}
			}
			this.frozenLib$hasCheckedGenericEntrypoints = true;

			if (newSource != null) {
				this.surfaceRule = SurfaceRules.sequence(newSource, this.surfaceRule, newSource);
			}
		}
	}

	@Override
	public void setDimension(Holder<DimensionType> dimension) {
		this.frozenLib$dimension = dimension;
	}
}
