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
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.api.FrozenPresetBoundRuleSource;
import net.frozenblock.lib.worldgen.surface.api.entrypoint.FrozenSurfaceRuleEntrypoint;
import net.frozenblock.lib.worldgen.surface.impl.SetNoiseGeneratorPresetInterface;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
public class NoiseGeneratorSettingsMixin implements SetNoiseGeneratorPresetInterface {

	@Shadow
	@Final
	@Mutable
	private SurfaceRules.RuleSource surfaceRule;

	@Unique
	private ResourceLocation frozenLib$preset;
	@Unique
	private boolean frozenLib$hasCheckedEntrypoints;

	@Inject(method = "surfaceRule", at = @At("HEAD"))
	private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		if (!this.frozenLib$hasCheckedEntrypoints) {
			ArrayList<FrozenPresetBoundRuleSource> sourceHolders = new ArrayList<>();

			FabricLoader.getInstance().getEntrypointContainers("frozenlib:surfacerules", FrozenSurfaceRuleEntrypoint.class).forEach(entrypoint -> {
				try {
					FrozenSurfaceRuleEntrypoint ruleEntrypoint = entrypoint.getEntrypoint();
					ruleEntrypoint.addRuleSources(sourceHolders);
				} catch (Throwable ignored) {

				}
			});

			SurfaceRules.RuleSource newSource = null;
			for (FrozenPresetBoundRuleSource presetBoundRuleSource : sourceHolders) {
				if (presetBoundRuleSource.preset.equals(this.frozenLib$preset)) {
					if (newSource == null) {
						newSource = presetBoundRuleSource.ruleSource;
					} else {
						newSource = SurfaceRules.sequence(newSource, presetBoundRuleSource.ruleSource);
					}
				}
			}
			this.frozenLib$hasCheckedEntrypoints = true;

			if (newSource != null) {
				this.surfaceRule = SurfaceRules.sequence(newSource, this.surfaceRule, newSource);
			}
		}
	}

	@Override
	public void setPreset(ResourceLocation location) {
		FrozenMain.log(location + "preset registered!", FabricLoader.getInstance().isDevelopmentEnvironment());
		this.frozenLib$preset = location;
	}
}

