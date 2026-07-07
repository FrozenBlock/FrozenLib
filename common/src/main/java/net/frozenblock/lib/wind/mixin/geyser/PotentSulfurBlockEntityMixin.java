/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.wind.mixin.geyser;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.frozenblock.lib.wind.disturbance.geyser.GeyserWindDisturbance;
import net.frozenblock.lib.wind.disturbance.geyser.PotentSulfurWindAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PotentSulfurBlockEntity.class)
public class PotentSulfurBlockEntityMixin implements PotentSulfurWindAccess {

	@Unique
	private AABB frozenLib$windArea = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
	@Unique
	private long frozenLib$lastActiveGameTime = Long.MIN_VALUE;

	@Unique
	@Override
	public void frozenLib$pingWindActive(AABB area, long gameTime) {
		this.frozenLib$windArea = area;
		this.frozenLib$lastActiveGameTime = gameTime;
	}

	@Unique
	@Override
	public AABB frozenLib$getWindArea() {
		return this.frozenLib$windArea;
	}

	@Unique
	@Override
	public boolean frozenLib$isWindActive(long currentGameTime) {
		return (currentGameTime - this.frozenLib$lastActiveGameTime) <= 1L;
	}

	@ModifyExpressionValue(
		method = "lambda$static$5",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
		)
	)
	private static AABB frozenLib$addWindDisturbanceToGeyser(
		AABB original,
		Level level, BlockPos pos, BlockState state, PotentSulfurBlockEntity entity
	) {
		final AABB area = original.inflate(0.5D).move(0D, 0.5D, 0D);
		entity.frozenLib$pingWindActive(area, level.getGameTime());
		if (level.isClientSide()) return original;

		WindDisturbances.addIf(
			level,
			entity,
			source -> WindDisturbances.noneMatch(source, WindDisturbances.type(WindDisturbanceType.GEYSER)),
			() -> GeyserWindDisturbance.INSTANCE
		);

		return original;
	}
}
