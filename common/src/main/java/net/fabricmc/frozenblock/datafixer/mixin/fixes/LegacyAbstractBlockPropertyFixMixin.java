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

package net.fabricmc.frozenblock.datafixer.mixin.fixes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.datafix.fixes.LegacyAbstractBlockPropertyFix;
import net.fabricmc.frozenblock.datafixer.api.fixes.BlockPropertyRenameAndFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * New mixin by FrozenBlock.
 */
@Mixin(LegacyAbstractBlockPropertyFix.class)
public class LegacyAbstractBlockPropertyFixMixin {

	@ModifyExpressionValue(
		method = "fixBlockState",
		at = @At(
			value = "CONSTANT",
			args = "stringValue=Name"
		)
	)
	private String frozenLib$useNewIdName(String original) {
		if (LegacyAbstractBlockPropertyFix.class.cast(this) instanceof BlockPropertyRenameAndFix) return "id";
		return original;
	}

	@ModifyExpressionValue(
		method = "fixBlockState",
		at = @At(
			value = "CONSTANT",
			args = "stringValue=Properties"
		)
	)
	private String frozenLib$useNewPropertiesName(String original) {
		if (LegacyAbstractBlockPropertyFix.class.cast(this) instanceof BlockPropertyRenameAndFix) return "properties";
		return original;
	}
}
