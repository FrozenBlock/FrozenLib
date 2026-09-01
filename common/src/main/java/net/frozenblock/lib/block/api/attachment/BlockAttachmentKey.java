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

package net.frozenblock.lib.block.api.attachment;

import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

/**
 * A key representing extra data to attach to a {@link Block}.
 */
public final class BlockAttachmentKey<T> {
	private final boolean persistent;
	private final Supplier<String> name;

	private BlockAttachmentKey(boolean persistent, Supplier<String> debugName) {
		this.persistent = persistent;
		this.name = debugName;
	}

	/**
	 * @param persistent Whether data associated with this key will remain when a {@link Block}'s attachment map is cleared.
	 * @param debugName The name of this Block Attachment Key, shown in error messages.
	 * @return a new Block Attachment Key.
	 */
	public static <T> BlockAttachmentKey<T> create(boolean persistent, Supplier<String> debugName) {
		return new BlockAttachmentKey<>(persistent, debugName);
	}

	/**
	 * @param debugName The name of this Block Attachment Key, shown in error messages.
	 * @return a new Block Attachment Key.
	 */
	public static <T> BlockAttachmentKey<T> create(Supplier<String> debugName) {
		return new BlockAttachmentKey<>(false, debugName);
	}

	/**
	 * @param persistent Whether data associated with this key will remain when a {@link Block}'s attachment map is cleared.
	 * @return a new Block Attachment Key.
	 */
	public static <T> BlockAttachmentKey<T> create(boolean persistent) {
		return new BlockAttachmentKey<>(persistent, () -> "unnamed");
	}

	/**
	 * @return a new Block Attachment Key.
	 */
	public static <T> BlockAttachmentKey<T> create() {
		return create(false);
	}

	public boolean persistent() {
		return this.persistent;
	}

	@Override
	public String toString() {
		return "BlockAttachmentKey(" + name.get() + ")";
	}
}
