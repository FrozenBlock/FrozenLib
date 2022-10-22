package net.frozenblock.lib.mixin.client;

import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
	protected M model;

    @Inject(method = "getRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"), cancellable = true)
    private void getEasterEgg(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        FrozenRegistry.ENTITY_TEXTURE_OVERRIDE.forEach(override -> {
            if (override.getType() == livingEntity.getType()) {
                var texture = override.getTexture();
                if (texture != null) {
                    if (override.getCondition().condition(livingEntity)) {
                        cir.setReturnValue(this.model.renderType(texture));
                    }
                }
            }
        });
    }

    @Inject(method = "getRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;itemEntityTranslucentCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"), cancellable = true)
    private void getItemEasterEgg(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        FrozenRegistry.ENTITY_TEXTURE_OVERRIDE.forEach(override -> {
            if (override.getType() == livingEntity.getType()) {
                var texture = override.getTexture();
                if (texture != null) {
                    if (override.getCondition().condition(livingEntity)) {
                        cir.setReturnValue(RenderType.itemEntityTranslucentCull(texture));
                    }
                }
            }
        });
    }

    @Inject(method = "getRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;outline(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;", shift = At.Shift.BEFORE), cancellable = true)
    private void getOutlineEasterEgg(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (glowing) {
            FrozenRegistry.ENTITY_TEXTURE_OVERRIDE.forEach(override -> {
                if (override.getType() == livingEntity.getType()) {
                    var texture = override.getTexture();
                    if (texture != null) {
                        if (override.getCondition().condition(livingEntity)) {
                            cir.setReturnValue(RenderType.outline(texture));
                        }
                    }
                }
            });
        }
    }
}
