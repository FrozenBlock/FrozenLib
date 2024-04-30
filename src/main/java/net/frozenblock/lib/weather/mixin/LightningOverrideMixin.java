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

package net.frozenblock.lib.weather.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.tag.api.FrozenBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public final class LightningOverrideMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRainingAt(Lnet/minecraft/core/BlockPos;)Z"), method = "tickChunk")
	public boolean frozenLib$getLightningTarget(ServerLevel serverLevel, BlockPos pos, Operation<Boolean> operation) {
		return this.frozenLib$newLightningCheck(pos);
	}

	@Unique
	public boolean frozenLib$newLightningCheck(BlockPos position) {
		ServerLevel level = ServerLevel.class.cast(this);
		if (!level.isRaining() || !level.canSeeSky(position)) {
			return false;
		}
		if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
			return false;
		}
		Holder<Biome> biome = level.getBiome(position);
		return (
			(biome.value().hasPrecipitation() && biome.value().warmEnoughToRain(position))
			|| biome.is(FrozenBiomeTags.CAN_LIGHTNING_OVERRIDE)
		) && !biome.is(FrozenBiomeTags.CANNOT_LIGHTNING_OVERRIDE);
	}

}
