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

package net.frozenblock.lib.sound.client.api.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class RelativeMovingSoundInstance extends AbstractTickableSoundInstance {
	private final Entity entity;
	private final double xOffset;
	private final double yOffset;
	private final double zOffset;

	public RelativeMovingSoundInstance(SoundEvent sound, SoundSource source, float f, float g, Entity entity, BlockPos pos, long l) {
		super(sound, source, RandomSource.create(l));
		this.volume = f;
		this.pitch = g;
		this.entity = entity;
		this.x = pos.getX() + 0.5D;
		this.y = pos.getY() + 0.5D;
		this.z = pos.getZ() + 0.5D;
		this.xOffset = this.x - entity.getX();
		this.yOffset = this.y - entity.getY();
		this.zOffset = this.z - entity.getZ();
	}

	@Override
	public boolean canPlaySound() {
		return !this.entity.isSilent();
	}

	@Override
	public void tick() {
		if (this.entity.isRemoved()) {
			this.stop();
			return;
		}
		this.x = this.entity.getX() + this.xOffset;
		this.y = this.entity.getY() + this.yOffset;
		this.z = this.entity.getZ() + this.zOffset;
	}
}
