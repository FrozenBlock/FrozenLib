/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.sound.impl.overwrite;

import java.util.function.BooleanSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class AbstractBlockSoundTypeOverwrite<T> {
	private final T value;
	private final SoundType soundType;
	private final BooleanSupplier soundCondition;

	public AbstractBlockSoundTypeOverwrite(T value, SoundType soundType, BooleanSupplier soundCondition) {
		this.value = value;
		this.soundType = soundType;
		this.soundCondition = soundCondition;
	}

	public T getValue() {
		return this.value;
	}

	public SoundType getSoundType() {
		return this.soundType;
	}

	public BooleanSupplier getSoundCondition() {
		return this.soundCondition;
	}

	public abstract boolean matches(Block block);
}
