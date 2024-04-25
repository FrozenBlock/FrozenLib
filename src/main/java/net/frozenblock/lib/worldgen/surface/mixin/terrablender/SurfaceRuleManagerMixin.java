/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.surface.mixin.terrablender;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrablender.api.SurfaceRuleManager;

@Mixin(SurfaceRuleManager.class)
public class SurfaceRuleManagerMixin {

	@ModifyReturnValue(method = "getNamespacedRules", at = @At("RETURN"))
	private static SurfaceRules.RuleSource frozenLib$getDefaultSurfaceRules(SurfaceRules.RuleSource original, SurfaceRuleManager.RuleCategory category, SurfaceRules.RuleSource fallback) {
		SurfaceRules.RuleSource newRules = FrozenSurfaceRules.getSurfaceRules(
			category == SurfaceRuleManager.RuleCategory.OVERWORLD
				? BuiltinDimensionTypes.OVERWORLD
				: BuiltinDimensionTypes.NETHER
		);

		if (newRules != null) {
			FrozenLogUtils.log("Applying FrozenLib's surface rules to TerraBlender", FrozenSharedConstants.UNSTABLE_LOGGING);
			return SurfaceRules.sequence(newRules, original, newRules);
		}
		return original;
	}
}
