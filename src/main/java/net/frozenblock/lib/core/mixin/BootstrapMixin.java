package net.frozenblock.lib.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.FrozenBools;
import net.minecraft.server.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(Bootstrap.class)
public class BootstrapMixin {

	@WrapOperation(method = "bootStrap", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicLong;set(J)V"))
	private static void finishBootStrap(AtomicLong instance, long newValue, Operation<Void> original) {
		FrozenBools.isInitialized = true;
		original.call(instance, newValue);
	}
}
