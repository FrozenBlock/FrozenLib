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

package net.frozenblock.lib.block.impl.attachment;

import net.frozenblock.lib.block.api.attachment.BlockAttachmentEvents;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentKey;
import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

/**
 * Allows custom data to be "attached" to {@link Block}s.
 * <p>
 * This can be used to improve the performance of features that would otherwise rely on holders such as
 * {@link java.util.List Lists}, {@link java.util.Map Maps}, {@link net.minecraft.core.Registry Registries}, or {@link net.frozenblock.lib.event.api.Event Events}
 * to control custom {@link Block}-specific behavior.
 * <p>
 * {@link #frozenLib$clearAttachments()} is called each time {@link CommonLifecycleEvents#TAGS_LOADED} is invoked,
 * which completely nullifies the inner {@link java.util.Map} containing all attached data.
 * <p>
 * As such, only {@link BlockAttachmentEvents#REGISTER} should be used to attach data to {@link Block}s.
 * <p>
 * Injected into {@link net.minecraft.world.level.block.state.BlockBehaviour BlockBehaviour} via mixin.
 */
public interface BlockAttachmentHolder {
	/**
	 * Gets attached data from a {@link Block}.
	 * @param key the {@link BlockAttachmentKey} of the attachment.
	 * @param <T> the type of the attachment.
	 * @return the attachment, or {@code null} if it cannot be found.
	 */
	@Nullable
	default <T> T frozenLib$getAttached(BlockAttachmentKey<T> key) {
		throw new AssertionError();
	}

	/**
	 * Gets attached data from a {@link Block}, or a default value if it cannot be found.
	 * @param key the {@link BlockAttachmentKey} of the attachment.
	 * @param defaultValue the value to return if the attachment cannot be found.
	 * @param <T> the type of the attachment.
	 * @return the attachment, or {@code null} if it cannot be found.
	 */
	@Nullable
	default <T> T frozenLib$getAttachedOrDefault(BlockAttachmentKey<T> key, T defaultValue) {
		throw new AssertionError();
	}

	/**
	 * Sets attached data to a {@link Block}.
	 * @param key the {@link BlockAttachmentKey} of the attachment.
	 * @param value the data to attach to the {@link Block}.
	 * @param <T> the type of the attachment.
	 */
	@Nullable
	default <T> void frozenLib$setAttached(BlockAttachmentKey<T> key, @Nullable T value) {
		throw new AssertionError();
	}

	/**
	 * Removes attached data from a {@link Block}.
	 * @param key the {@link BlockAttachmentKey} of the attachment.
	 * @param <T> the type of the attachment.
	 * @return the removed data, or {@code null} if it cannot be found.
	 */
	@Nullable
	default <T> T frozenLib$removeAttached(BlockAttachmentKey<T> key) {
		throw new AssertionError();
	}

	/**
	 * Sets the inner {@link java.util.Map} containing all attached data to {@code null}.
	 * <p>
	 * This was chosen over {@link Map#clear()} solely because it is slightly faster. Per my research, an average of 2 nanoseconds faster...
	 */
	default void frozenLib$clearAttachments() {
		throw new AssertionError();
	}
}
