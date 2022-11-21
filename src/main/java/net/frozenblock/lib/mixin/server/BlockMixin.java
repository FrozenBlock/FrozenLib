/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.server;

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
		String namespace = id.getNamespace();
		if (BlockSoundGroupOverwrites.BLOCK_SOUNDS.containsKey(id)) {
			info.setReturnValue(BlockSoundGroupOverwrites.BLOCK_SOUNDS.get(id));
		} else if (BlockSoundGroupOverwrites.NAMESPACE_SOUNDS.containsKey(namespace)) {
			info.setReturnValue(BlockSoundGroupOverwrites.NAMESPACE_SOUNDS.get(namespace));
		}
	}

}
