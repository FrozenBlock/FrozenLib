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
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorInterface {

	@Unique
	private SurfaceRules.RuleSource frozenLib$newSurfaceRule;
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

	@Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
	private void frozenLib$modifyRules(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		SurfaceRules.RuleSource returnValue = cir.getReturnValue();
		SurfaceRules.RuleSource newRule = null;

		if (this.frozenLib$dimension != null) {
			boolean overworldCaves = this.frozenLib$dimension.is(BuiltinDimensionTypes.OVERWORLD_CAVES);
			boolean isOverworld = this.frozenLib$dimension.is(BuiltinDimensionTypes.OVERWORLD) || overworldCaves;
			boolean isNether = this.frozenLib$dimension.is(BuiltinDimensionTypes.NETHER);

			//OVERWORLD
			if (!this.frozenLib$hasCheckedOverworldEntrypoints && isOverworld) {
				ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

				//TODO: Fix i guess idk
				SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().addOverworldSurfaceRules(sourceHolders);

				FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRules(sourceHolders)));

				if (FrozenBools.HAS_C2ME) {
					sourceHolders.add(SurfaceRuleData.overworldLike(!overworldCaves, overworldCaves, true));
				}

				SurfaceRules.RuleSource newSource = null;
				for (SurfaceRules.RuleSource ruleSource : sourceHolders) {
					if (newSource == null) {
						newSource = ruleSource;
					} else {
						newSource = SurfaceRules.sequence(newSource, ruleSource);
					}
				}

				if (newSource != null) {
					newSource = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), newSource);
				}

				ArrayList<SurfaceRules.RuleSource> noPrelimSourceHolders = new ArrayList<>();

				//TODO: Fix i guess idk
				SurfaceRuleEvents.MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE.invoker().addOverworldNoPrelimSurfaceRules(noPrelimSourceHolders);

				FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRulesNoPrelimSurface(noPrelimSourceHolders)));

				SurfaceRules.RuleSource noPrelimSource = null;
				for (SurfaceRules.RuleSource ruleSource : noPrelimSourceHolders) {
					if (noPrelimSource == null) {
						noPrelimSource = ruleSource;
					} else {
						noPrelimSource = SurfaceRules.sequence(noPrelimSource, ruleSource);
					}
				}

				this.frozenLib$hasCheckedOverworldEntrypoints = true;

				if (newSource != null) {
					newRule = SurfaceRules.sequence(newSource);
				}
				if (noPrelimSource != null) {
					if (newRule != null) {
						newRule = SurfaceRules.sequence(noPrelimSource, newRule);
					} else {
						newRule = SurfaceRules.sequence(noPrelimSource);
					}
				}
			} else {
				this.frozenLib$hasCheckedOverworldEntrypoints = true;
			}

			//NETHER
			if (!this.frozenLib$hasCheckedNetherEntrypoints && isNether) {
				ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

				//TODO: Fix i guess idk
				SurfaceRuleEvents.MODIFY_NETHER.invoker().addNetherSurfaceRules(sourceHolders);

				FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addNetherSurfaceRules(sourceHolders)));

				if (FrozenBools.HAS_C2ME) {
					sourceHolders.add(SurfaceRuleData.nether());
				}

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
					if (newRule != null) {
						newRule = SurfaceRules.sequence(newSource, newRule);
					} else {
						newRule = SurfaceRules.sequence(newSource);
					}
				}
			} else {
				this.frozenLib$hasCheckedNetherEntrypoints = true;
			}

			//END
			if (!this.frozenLib$hasCheckedEndEntrypoints && this.frozenLib$dimension.is(BuiltinDimensionTypes.END)) {
				ArrayList<SurfaceRules.RuleSource> sourceHolders = new ArrayList<>();

				//TODO: Fix i guess idk
				SurfaceRuleEvents.MODIFY_END.invoker().addEndSurfaceRules(sourceHolders);

				FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addEndSurfaceRules(sourceHolders)));

				if (FrozenBools.HAS_C2ME) {
					sourceHolders.add(SurfaceRuleData.end());
				}

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
					if (newRule != null) {
						newRule = SurfaceRules.sequence(newSource, newRule);
					} else {
						newRule = SurfaceRules.sequence(newSource);
					}
				}
			} else {
				this.frozenLib$hasCheckedEndEntrypoints = true;
			}

			//GENERIC / ALL LEVEL STEMS
			if (!this.frozenLib$hasCheckedGenericEntrypoints) {
				ArrayList<FrozenDimensionBoundRuleSource> sourceHolders = new ArrayList<>();

				//TODO: Fix i guess idk
				SurfaceRuleEvents.MODIFY_GENERIC.invoker().addGenericSurfaceRules(sourceHolders);

				FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addSurfaceRules(sourceHolders)));

				SurfaceRules.RuleSource newSource = null;
				for (FrozenDimensionBoundRuleSource dimRuleSource : sourceHolders) {
					if (this.frozenLib$dimension.is(dimRuleSource.dimension())) {
						if (newSource == null) {
							newSource = dimRuleSource.ruleSource();
						} else {
							newSource = SurfaceRules.sequence(newSource, dimRuleSource.ruleSource());
						}
					}
				}
				this.frozenLib$hasCheckedGenericEntrypoints = true;

				if (newSource != null) {
					if (newRule != null) {
						newRule = SurfaceRules.sequence(newSource, newRule);
					} else {
						newRule = SurfaceRules.sequence(newSource);
					}
				}
			}

			if (newRule != null) {
				this.frozenLib$newSurfaceRule = SurfaceRules.sequence(newRule, returnValue);
			}

			if (this.frozenLib$newSurfaceRule != null) {
				cir.setReturnValue(this.frozenLib$newSurfaceRule);
			}
		}
	}

	@Unique
	@Override
	public void setDimension(Holder<DimensionType> dimension) {
		this.frozenLib$dimension = dimension;
	}

}
