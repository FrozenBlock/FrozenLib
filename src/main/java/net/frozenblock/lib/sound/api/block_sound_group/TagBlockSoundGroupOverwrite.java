package net.frozenblock.lib.sound.api.block_sound_group;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public record TagBlockSoundGroupOverwrite(@NotNull TagKey<Block> blockTag, @NotNull SoundType soundOverwrite, BooleanSupplier condition) {
}
