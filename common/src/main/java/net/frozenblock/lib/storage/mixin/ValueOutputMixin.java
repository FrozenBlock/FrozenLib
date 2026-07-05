package net.frozenblock.lib.storage.mixin;

import net.frozenblock.lib.storage.impl.ValueOutputExtension;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ValueOutput.class)
public interface ValueOutputMixin extends ValueOutputExtension {
}
