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
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class HolderSetBlockSoundTypeOverwrite extends AbstractBlockSoundTypeOverwrite<HolderSet<Block>> {

	public HolderSetBlockSoundTypeOverwrite(HolderSet<Block> value, SoundType soundType, BooleanSupplier condition) {
		super(value, soundType, condition);
	}

	@Override
	public boolean matches(BlockState state) {
		return state.is(this.getValue());
	}
}
