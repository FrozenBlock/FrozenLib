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

package net.frozenblock.lib.block.mixin.attachment;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentEvents;
import net.frozenblock.lib.block.impl.attachment.BlockAttachmentHolder;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin implements BlockAttachmentHolder {
	@Unique
	@Nullable
	private Map<BlockAttachmentKey<?>, Object> frozenLib$attachments;

	@Unique
	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T frozenLib$getAttached(BlockAttachmentKey<T> key) {
		if (this.frozenLib$attachments == null) return null;
		return (T) this.frozenLib$attachments.get(key);
	}

	@Unique
	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T frozenLib$getAttachedOrDefault(BlockAttachmentKey<T> key, @Nullable T defaultValue) {
		if (this.frozenLib$attachments == null) return defaultValue;
		return (T) this.frozenLib$attachments.getOrDefault(key, defaultValue);
	}

	@Unique
	@Nullable
	@Override
	public <T> void frozenLib$setAttached(BlockAttachmentKey<T> key, @Nullable T value) {
		// I saw Fabric used Reference2ObjectOpenHashMap and looked into why that's the case.
		// Reference2ObjectOpenHashMap apparently uses == instead of .equals, which is... well.
		// Just better and safer overall in this context.
		// So, I opted to use it here because why not?
		if (this.frozenLib$attachments == null) this.frozenLib$attachments = new Reference2ObjectOpenHashMap<>();
		this.frozenLib$attachments.put(key, value);
		if (BlockBehaviour.class.cast(this) instanceof Block block) BlockAttachmentEvents.ON_SET.invoker().onSet(block, key, value);
	}

	@Unique
	@Nullable
	@Override
	public <T> T frozenLib$removeAttached(BlockAttachmentKey<T> key) {
		if (this.frozenLib$attachments == null) return null;
		return (T) this.frozenLib$attachments.remove(key);
	}

	@Unique
	@Override
	public void frozenLib$clearAttachments() {
		this.frozenLib$attachments = null;
	}
}
