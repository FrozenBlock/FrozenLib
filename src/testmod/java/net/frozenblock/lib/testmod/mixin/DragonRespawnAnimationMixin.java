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

package net.frozenblock.lib.testmod.mixin;

import java.util.List;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DragonRespawnAnimationMixin {

	@Mixin(targets = "net/minecraft/world/level/dimension/end/DragonRespawnAnimation$2")
	private static class PreparingPillarsMixin {

		@Inject(method = "tick", at = @At("HEAD"))
		private void startShaking(ServerLevel world, EndDragonFight fight, List<EndCrystal> crystals, int i, BlockPos pos, CallbackInfo ci) {
			if (i == 0) {
				ScreenShakeManager.addScreenShake(world, 0.4F, 60, 0, 130, 0, 180);
			}
		}
	}

	@Mixin(targets = "net/minecraft/world/level/dimension/end/DragonRespawnAnimation$4")
	private static class SpawningDragonMixin {

		@Inject(method = "tick", at = @At("TAIL"))
		private void startShaking(ServerLevel world, EndDragonFight fight, List<EndCrystal> crystals, int i, BlockPos pos, CallbackInfo ci) {
			if (i == 0) {
				ScreenShakeManager.addScreenShake(world, 0.7F, 140, 0, 130, 0, 180);
			}
		}
	}
}
