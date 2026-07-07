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

package net.frozenblock.lib.storage.mixin;

import net.frozenblock.lib.storage.impl.ValueOutputExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagValueOutput.class)
public class TagValueOutputMixin implements ValueOutputExtension {

	@Shadow
	@Final
	private CompoundTag output;

	@Override
	public void frozenLib$putByteArray(String key, byte[] value) {
		this.output.putByteArray(key, value);
	}

	@Override
	public void frozenLib$putLongArray(String key, long[] value) {
		this.output.putLongArray(key, value);
	}
}
