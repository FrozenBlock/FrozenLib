package org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.function.UnaryOperator;
import net.minecraft.util.datafix.fixes.LegacyBlockPropertyRenameAndFix;

/**
 * An alternate implementation of {@link LegacyBlockPropertyRenameAndFix} that actually works on >=26.3-snapshot-7.
 * @see org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin.fixes.LegacyAbstractBlockPropertyFixMixin this mixin for implementation.
 */
public class BlockPropertyRenameAndFix extends LegacyBlockPropertyRenameAndFix {

	public BlockPropertyRenameAndFix(Schema outputSchema, String name, String blockId, String oldPropertyName, String newPropertyName, UnaryOperator<String> valueFixer) {
		super(outputSchema, name, blockId, oldPropertyName, newPropertyName, valueFixer);
	}
}
