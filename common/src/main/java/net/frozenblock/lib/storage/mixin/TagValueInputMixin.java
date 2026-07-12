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

import java.util.Collection;
import java.util.Optional;
import net.frozenblock.lib.storage.impl.ValueInputExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TagValueInput.class)
public class TagValueInputMixin implements ValueInputExtension {

	@Shadow
	@Final
	private CompoundTag input;

	@Unique
	@Override
	public Collection<String> frozenLib$keySet() {
		return this.input.keySet();
	}

	@Unique
	@Override
	public boolean frozenLib$contains(String key) {
		return this.input.contains(key);
	}

	@Unique
	@Override
	public Optional<byte[]> frozenLib$getOptionalByteArray(String key) {
		return this.input.getByteArray(key);
	}

	@Unique
	@Override
	public Optional<long[]> frozenLib$getOptionalLongArray(String key) {
		return this.input.getLongArray(key);
	}
}
