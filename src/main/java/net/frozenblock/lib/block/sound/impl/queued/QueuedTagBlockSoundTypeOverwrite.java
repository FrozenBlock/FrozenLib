package net.frozenblock.lib.block.sound.impl.queued;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import net.frozenblock.lib.tag.api.TagUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class QueuedTagBlockSoundTypeOverwrite extends AbstractQueuedBlockSoundTypeOverwrite<TagKey<Block>> {

	public QueuedTagBlockSoundTypeOverwrite(TagKey<Block> value, SoundType soundType, BooleanSupplier soundCondition) {
		super(value, soundType, soundCondition);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, AbstractQueuedBlockSoundTypeOverwrite<TagKey<Block>>> consumer) {
		TagUtils.getAllEntries(this.getValue()).forEach(block -> {
				ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
				if (!location.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
					consumer.accept(location, this);
				}
			}
		);
	}
}
