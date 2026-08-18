package net.frozenblock.lib.block.api.attachment;

import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

/**
 * A key representing extra data to attach to a {@link Block}.
 */
public final class BlockAttachmentKey<T> {
	private final Supplier<String> name;

	private BlockAttachmentKey(Supplier<String> debugName) {
		this.name = debugName;
	}

	/**
	 * @param debugName The name of this Block Attachment Key, shown in error messages.
	 * @return a new Block Attachment Key.
	 */
	public static <T> BlockAttachmentKey<T> create(Supplier<String> debugName) {
		return new BlockAttachmentKey<>(debugName);
	}

	/**
	 * @return a new Block Attachment Key.
	 */
	public static <T> BlockAttachmentKey<T> create() {
		return new BlockAttachmentKey<>(() -> "unnamed");
	}

	@Override
	public String toString() {
		return "BlockAttachmentKey(" + name.get() + ")";
	}
}
