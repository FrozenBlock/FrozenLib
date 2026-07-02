package net.frozenblock.lib.platform.mixin.data;

import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements DataAttachmentTarget {
}
