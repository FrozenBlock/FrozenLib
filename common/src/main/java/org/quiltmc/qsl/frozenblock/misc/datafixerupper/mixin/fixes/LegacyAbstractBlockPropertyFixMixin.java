package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin.fixes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.datafix.fixes.LegacyAbstractBlockPropertyFix;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.api.fixes.BlockPropertyRenameAndFix;
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
