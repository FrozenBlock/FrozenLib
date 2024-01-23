/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.entity.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.Level;

/**
 * This is the same as {@link AbstractFish} but the entity will not flop when on land.
 */
public abstract class NoFlopAbstractFish extends AbstractFish {

	public NoFlopAbstractFish(EntityType<? extends NoFlopAbstractFish> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected SoundEvent getFlopSound() {
		return null;
	}

	/**
	 * Acts as a form of access widener.
	 */
	public boolean canRandomSwim() {
		return super.canRandomSwim();
	}
}
