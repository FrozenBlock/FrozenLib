package net.fabricmc.frozenblock.datafixer.mixin.fixes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.datafix.fixes.LegacyBlockRenameFix;
import net.fabricmc.frozenblock.datafixer.api.fixes.BlockRenameFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * New mixin by FrozenBlock.
 */
@Mixin(LegacyBlockRenameFix.class)
public class LegacyBlockRenameFixMixin {

	@ModifyExpressionValue(
		method = "fixBlockState",
		at = @At(
			value = "CONSTANT",
			args = "stringValue=Name"
		)
	)
	private String frozenLib$useNewIdName(String original) {
		if (LegacyBlockRenameFix.class.cast(this) instanceof BlockRenameFix) return "id";
		return original;
	}
}
