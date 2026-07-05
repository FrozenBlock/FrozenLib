package net.frozenblock.lib.storage.mixin;

import net.frozenblock.lib.storage.impl.ValueOutputExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagValueOutput.class)
public class TagValueOutputMixin implements ValueOutputExtension {

	@Shadow
	@Final
	private CompoundTag output;

	@Override
	public void frozenLib$putByteArray(String key, byte[] value) {
		this.output.putByteArray(key, value);
	}

	@Override
	public void frozenLib$putLongArray(String key, long[] value) {
		this.output.putLongArray(key, value);
	}
}
