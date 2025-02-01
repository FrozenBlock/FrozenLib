package net.frozenblock.lib.block.sound.impl.queued;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class QueuedBlockSoundTypeOverwrite extends AbstractQueuedBlockSoundTypeOverwrite<Block> {

	public QueuedBlockSoundTypeOverwrite(Block value, SoundType soundType, BooleanSupplier soundCondition) {
		super(value, soundType, soundCondition);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, AbstractQueuedBlockSoundTypeOverwrite<Block>> consumer) {
		ResourceLocation location = BuiltInRegistries.BLOCK.getKey(this.getValue());
		if (!location.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
			consumer.accept(location, this);
		}
	}
}
