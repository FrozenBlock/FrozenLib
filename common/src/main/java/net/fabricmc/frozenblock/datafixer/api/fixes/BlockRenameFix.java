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
