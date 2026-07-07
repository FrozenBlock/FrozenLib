package net.frozenblock.lib.core.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(RegistrySetBuilder.BuildState.class)
public class RegistryBuildStateMixin {

	@WrapOperation(method = "reportNotCollectedHolders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private boolean ignoreMissingBiomes(List<RuntimeException> instance, Object e, Operation<Boolean> original, @Local(name = "key") ResourceKey<Object> key) {
		if (!key.registryKey().equals(Registries.BIOME)) {
			return original.call(instance, e);
		} else {
			return false;
		}
	}
}
