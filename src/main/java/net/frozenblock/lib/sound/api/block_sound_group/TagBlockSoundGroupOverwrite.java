package net.frozenblock.lib.sound.api.block_sound_group;

import java.util.function.BooleanSupplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

public record TagBlockSoundGroupOverwrite(@NotNull TagKey<Block> blockTag, @NotNull SoundType soundOverwrite, BooleanSupplier condition) {
}
