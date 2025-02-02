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
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class TagBlockSoundTypeOverwrite extends AbstractBlockSoundTypeOverwrite<TagKey<Block>> {

	public TagBlockSoundTypeOverwrite(TagKey<Block> value, SoundType soundType, BooleanSupplier soundCondition) {
		super(value, soundType, soundCondition);
	}

	@Override
	public boolean matches(@NotNull Block block) {
		return block.builtInRegistryHolder().is(this.getValue());
	}
}
