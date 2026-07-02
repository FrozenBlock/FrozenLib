package net.frozenblock.lib.platform.mixin.data;

import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements DataAttachmentTarget {
}
