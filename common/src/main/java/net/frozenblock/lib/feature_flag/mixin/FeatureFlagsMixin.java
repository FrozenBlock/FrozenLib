/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.feature_flag.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureFlags.class)
public class FeatureFlagsMixin {

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/flag/FeatureFlagRegistry$Builder;createVanilla(Ljava/lang/String;)Lnet/minecraft/world/flag/FeatureFlag;",
			ordinal = 0
		)
	)
	private static void frozenLib$save(
		CallbackInfo info,
		@Local FeatureFlagRegistry.Builder builder
	) {
		FeatureFlagApi.builder = builder;
	}
}
