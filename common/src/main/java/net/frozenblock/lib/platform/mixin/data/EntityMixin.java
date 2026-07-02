package net.frozenblock.lib.platform.mixin.data;

import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin implements DataAttachmentTarget {
}
