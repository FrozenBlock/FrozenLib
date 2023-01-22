/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.mixin.client;

import net.frozenblock.lib.sound.api.block_sound_group.BlockSoundGroupOverwrite;
import net.frozenblock.lib.sound.api.block_sound_group.BlockSoundGroupOverwrites;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public final class BlockMixin {

    @Inject(method = "getSoundType", at = @At("RETURN"), cancellable = true)
    private void getSoundGroupOverride(BlockState state, CallbackInfoReturnable<SoundType> info) {
        Block block = state.getBlock();
        ResourceLocation id = Registry.BLOCK.getKey(block);
		var overwrites = BlockSoundGroupOverwrites.getOverwrites();
		if (overwrites != null) {
			for (BlockSoundGroupOverwrite overwrite : overwrites) {
				if (overwrite.blockId().equals(id)) {
					info.setReturnValue(overwrite.soundOverwrite());
				}
			}
		}
    }

}
