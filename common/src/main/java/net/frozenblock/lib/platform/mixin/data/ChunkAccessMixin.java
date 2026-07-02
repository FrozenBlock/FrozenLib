package net.frozenblock.lib.platform.mixin.data;

import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements DataAttachmentTarget {
}
