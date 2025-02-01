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
import net.frozenblock.lib.tag.api.TagUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class QueuedTagBlockSoundTypeOverwrite extends AbstractQueuedBlockSoundTypeOverwrite<TagKey<Block>> {

	public QueuedTagBlockSoundTypeOverwrite(TagKey<Block> value, SoundType soundType, BooleanSupplier soundCondition) {
		super(value, soundType, soundCondition);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, AbstractQueuedBlockSoundTypeOverwrite<TagKey<Block>>> consumer) {
		TagUtils.getAllEntries(this.getValue()).forEach(block -> {
				ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
				if (!location.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
					consumer.accept(location, this);
				}
			}
		);
	}
}
