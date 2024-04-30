/*
 * Copyright (C) 2024 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.mixin.client;

import net.frozenblock.lib.sound.api.block_sound_group.BlockSoundGroupOverwrite;
import net.frozenblock.lib.sound.api.block_sound_group.BlockSoundGroupOverwrites;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public final class BlockBehaviourMixin {

    @Inject(method = "getSoundType", at = @At("RETURN"), cancellable = true)
    private void getSoundGroupOverride(BlockState state, CallbackInfoReturnable<SoundType> info) {
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
		var overwrites = BlockSoundGroupOverwrites.getOverwrites();
		if (overwrites != null) {
			for (BlockSoundGroupOverwrite overwrite : overwrites) {
				if (overwrite.blockId().equals(id) && overwrite.condition().getAsBoolean()) {
					info.setReturnValue(overwrite.soundOverwrite());
				}
			}
		}
    }

}
