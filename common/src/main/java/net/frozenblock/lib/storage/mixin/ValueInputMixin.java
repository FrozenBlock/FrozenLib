package net.frozenblock.lib.storage.mixin;

import net.frozenblock.lib.storage.impl.ValueInputExtension;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ValueInput.class)
public interface ValueInputMixin extends ValueInputExtension {
}
