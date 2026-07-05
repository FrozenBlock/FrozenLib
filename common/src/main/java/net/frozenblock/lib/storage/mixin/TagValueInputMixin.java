package net.frozenblock.lib.storage.mixin;

import java.util.Collection;
import java.util.Optional;
import net.frozenblock.lib.storage.impl.ValueInputExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagValueInput.class)
public class TagValueInputMixin implements ValueInputExtension {

	@Shadow
	@Final
	private CompoundTag input;

	@Override
	public Collection<String> frozenLib$keySet() {
		return this.input.keySet();
	}

	@Override
	public boolean frozenLib$contains(String key) {
		return this.input.contains(key);
	}

	@Override
	public Optional<byte[]> frozenLib$getOptionalByteArray(String key) {
		return this.input.getByteArray(key);
	}

	@Override
	public Optional<long[]> frozenLib$getOptionalLongArray(String key) {
		return this.input.getLongArray(key);
	}
}
