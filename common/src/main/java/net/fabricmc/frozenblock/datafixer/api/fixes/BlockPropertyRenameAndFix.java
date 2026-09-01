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

package net.fabricmc.frozenblock.datafixer.api.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.function.UnaryOperator;
import net.minecraft.util.datafix.fixes.LegacyBlockPropertyRenameAndFix;

/**
 * An alternate implementation of {@link LegacyBlockPropertyRenameAndFix} that actually works on >=26.3-snapshot-7.
 * @see net.fabricmc.frozenblock.datafixer.mixin.fixes.LegacyAbstractBlockPropertyFixMixin this mixin for implementation.
 */
public class BlockPropertyRenameAndFix extends LegacyBlockPropertyRenameAndFix {

	public BlockPropertyRenameAndFix(Schema outputSchema, String name, String blockId, String oldPropertyName, String newPropertyName, UnaryOperator<String> valueFixer) {
		super(outputSchema, name, blockId, oldPropertyName, newPropertyName, valueFixer);
	}
}
