package net.fabricmc.frozenblock.datafixer.api.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.LegacyBlockRenameFix;
import java.util.function.Function;

/**
 * An alternate implementation of {@link LegacyBlockRenameFix} that actually works on >=26.3-snapshot-7.
 * @see net.fabricmc.frozenblock.datafixer.mixin.fixes.LegacyBlockRenameFixMixin this mixin for implementation.
 */
public abstract class BlockRenameFix extends LegacyBlockRenameFix {

	public BlockRenameFix(Schema outputSchema, String name) {
		super(outputSchema, name);
	}

	public static DataFix create(Schema outputSchema, String name, Function<String, String> renamer) {
		return new BlockRenameFix(outputSchema, name) {
			@Override
			protected String renameBlock(String block) {
				return renamer.apply(block);
			}
		};
	}
}
