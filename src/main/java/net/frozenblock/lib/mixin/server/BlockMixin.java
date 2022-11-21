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
