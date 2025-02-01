package net.frozenblock.lib.block.sound.impl.queued;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class QueuedResourceLocationBlockSoundTypeOverwrite extends AbstractQueuedBlockSoundTypeOverwrite<ResourceLocation> {

	public QueuedResourceLocationBlockSoundTypeOverwrite(ResourceLocation value, SoundType soundType, BooleanSupplier soundCondition) {
		super(value, soundType, soundCondition);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, AbstractQueuedBlockSoundTypeOverwrite<ResourceLocation>> consumer) {
		ResourceLocation location = this.getValue();
		if (BuiltInRegistries.BLOCK.containsKey(location)) {
			consumer.accept(location, this);
		}
	}
}
