package net.frozenblock.lib.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.MovingSoundLoopWithRestriction;
import net.frozenblock.lib.sound.StartingSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Shadow
    public void queueTickingSound(TickableSoundInstance tickableSound) {
    }

    @Inject(method = "stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V"))
    private void stop(SoundInstance sound, CallbackInfo ci) {
        if (sound instanceof StartingSoundInstance startingSound) {
            this.queueTickingSound(new MovingSoundLoopWithRestriction(startingSound.entity, startingSound.loopingSound, startingSound.getSource(), startingSound.getVolume(), startingSound.getPitch(), startingSound.predicate));
        }
    }
}
