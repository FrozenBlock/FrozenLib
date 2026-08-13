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

package net.frozenblock.lib.particle.client.api;

import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@ClientOnly
public abstract class ParticleSpawner {
	private final float spawnProbability;
	private final int spawnAttempts;
	private final int horizontalDistance;
	private final int minVerticalDistance;
	private final int maxVerticalDistance;
	private final boolean canSpawnInBlocks;

	protected ParticleSpawner(
		float spawnProbability,
		int spawnAttempts,
		int horizontalDistance,
		int minVerticalDistance,
		int maxVerticalDistance,
		boolean canSpawnInBlocks
	) {
		if (minVerticalDistance >= maxVerticalDistance) throw new IllegalArgumentException("minVerticalDistance cannot be greater than or equal to maxVerticalDistance!");
		this.spawnProbability = spawnProbability;
		this.spawnAttempts = spawnAttempts;
		this.horizontalDistance = horizontalDistance;
		this.minVerticalDistance = minVerticalDistance;
		this.maxVerticalDistance = maxVerticalDistance;
		this.canSpawnInBlocks = canSpawnInBlocks;
	}

	public abstract boolean canSpawnAtPos(Level level, BlockPos pos);

	public abstract ParticleOptions selectParticleOptions(Level level, BlockPos pos, RandomSource random);

	public void tick(Level level, BlockPos pos, RandomSource random) {
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int i = 0; i < this.spawnAttempts; ++i) {
			if (random.nextFloat() >= this.spawnProbability) continue;
			mutable.setWithOffset(
				pos,
				Mth.nextInt(random, -this.horizontalDistance, this.horizontalDistance),
				Mth.nextInt(random, this.minVerticalDistance, this.maxVerticalDistance),
				Mth.nextInt(random, -this.horizontalDistance, this.horizontalDistance)
			);

			final BlockState insideState = level.getBlockState(mutable);
			if ((!this.canSpawnInBlocks && insideState.isCollisionShapeFullBlock(level, mutable)) || !this.canSpawnAtPos(level, mutable)) continue;

			level.addParticle(
				this.selectParticleOptions(level, mutable, random),
				mutable.getX() + random.nextDouble(),
				mutable.getY() + random.nextDouble(),
				mutable.getZ() + random.nextDouble(),
				0D, 0D, 0D
			);
		}
	}
}
