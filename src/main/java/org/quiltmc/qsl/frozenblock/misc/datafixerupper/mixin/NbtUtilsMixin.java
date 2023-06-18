package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NbtUtils.class)
public class NbtUtilsMixin {

	@ModifyReturnValue(method = "addDataVersion", at = @At("RETURN"))
	private static CompoundTag addDataVersion(CompoundTag original) {
		return QuiltDataFixesInternals.get().addModDataVersions(original);
	}
}
