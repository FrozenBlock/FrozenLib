package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.tags.FrozenEntityTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    public void setTarget(@Nullable LivingEntity livingEntity, CallbackInfo info) {
        if (livingEntity != null) {
            if (livingEntity.getType().is(FrozenEntityTags.CREEPER_IGNORES)) {
                info.cancel();
            }
        }
    }

}
