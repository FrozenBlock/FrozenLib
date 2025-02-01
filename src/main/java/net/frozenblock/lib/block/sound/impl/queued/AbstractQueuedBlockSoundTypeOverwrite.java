package net.frozenblock.lib.block.sound.impl.queued;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class AbstractQueuedBlockSoundTypeOverwrite<T> {
	private final T value;
	private final SoundType soundType;
	private final BooleanSupplier soundCondition;

	public AbstractQueuedBlockSoundTypeOverwrite(T value, SoundType soundType, BooleanSupplier soundCondition) {
		this.value = value;
		this.soundType = soundType;
		this.soundCondition = soundCondition;
	}

	public T getValue() {
		return this.value;
	}

	public SoundType getSoundType() {
		return this.soundType;
	}

	public BooleanSupplier getSoundCondition() {
		return this.soundCondition;
	}

	public abstract void accept(BiConsumer<ResourceLocation, AbstractQueuedBlockSoundTypeOverwrite<T>> consumer);
}
