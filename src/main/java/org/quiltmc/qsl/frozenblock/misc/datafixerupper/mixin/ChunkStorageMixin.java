package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.storage.DataVersion;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkStorage.class)
public class ChunkStorageMixin {

	@WrapOperation(method = "upgradeChunkTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/DataVersion;getVersion()I"))
	private int bypassCheck(DataVersion instance, Operation<Integer> original) {
		if (!QuiltDataFixesInternals.get().isEmpty()) {
			return -1;
		}

		return original.call(instance);
	}
}
