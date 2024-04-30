/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.surface.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.worldgen.surface.impl.NoiseGeneratorInterface;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NoiseGeneratorSettings.class, priority = 990) // Apply before default mods
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorInterface {

	/**
	 * Surface rules added by FrozenLib
	 */
	@Unique
	private SurfaceRules.RuleSource frozenLib$frozenSurfaceRules;

	@ModifyReturnValue(method = "surfaceRule", at = @At("RETURN"))
	private SurfaceRules.RuleSource frozenLib$modifyRules(SurfaceRules.RuleSource original) {
		if (this.frozenLib$frozenSurfaceRules != null) {
			return SurfaceRules.sequence(this.frozenLib$frozenSurfaceRules, original);
		}

		return original;
	}

	@Unique
	@Override
	public void writeSurfaceRules(SurfaceRules.RuleSource surfaceRule) {
		if (surfaceRule == null || surfaceRule == this.frozenLib$frozenSurfaceRules) return;

		this.frozenLib$frozenSurfaceRules = surfaceRule;
	}

}
