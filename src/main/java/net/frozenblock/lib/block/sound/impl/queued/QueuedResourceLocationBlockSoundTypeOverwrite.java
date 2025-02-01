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

package net.frozenblock.lib.block.sound.impl.queued;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class QueuedResourceLocationBlockSoundTypeOverwrite extends AbstractQueuedBlockSoundTypeOverwrite<ResourceLocation> {

	public QueuedResourceLocationBlockSoundTypeOverwrite(ResourceLocation value, SoundType soundType, BooleanSupplier soundCondition) {
		super(value, soundType, soundCondition);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, AbstractQueuedBlockSoundTypeOverwrite<ResourceLocation>> consumer) {
		ResourceLocation location = this.getValue();
		if (BuiltInRegistries.BLOCK.containsKey(location)) {
			consumer.accept(location, this);
		}
	}
}
