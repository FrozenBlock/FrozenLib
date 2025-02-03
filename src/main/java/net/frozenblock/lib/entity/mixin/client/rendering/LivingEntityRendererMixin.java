/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.entity.mixin.client.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.registry.client.FrozenLibClientRegistries;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    @Shadow
	protected M model;

	protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
		super(context);
	}

	@Inject(
		method = "getRenderType",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/model/EntityModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
		),
		cancellable = true
	)
    private void frozenLib$getEasterEgg(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        FrozenLibClientRegistries.ENTITY_TEXTURE_OVERRIDE.forEach(override -> {
            if (override.type() == livingEntity.getType()) {
                var texture = override.texture();
                if (texture != null) {
                    if (override.condition().condition(livingEntity)) {
                        cir.setReturnValue(this.model.renderType(texture));
                    }
                }
            }
        });
    }

    @Inject(
		method = "getRenderType",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/RenderType;itemEntityTranslucentCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
		),
		cancellable = true
	)
    private void frozenLib$getItemEasterEgg(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
		FrozenLibClientRegistries.ENTITY_TEXTURE_OVERRIDE.forEach(override -> {
            if (override.type() == livingEntity.getType()) {
                var texture = override.texture();
                if (texture != null) {
                    if (override.condition().condition(livingEntity)) {
                        cir.setReturnValue(RenderType.itemEntityTranslucentCull(texture));
                    }
                }
            }
        });
    }

    @Inject(
		method = "getRenderType",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/RenderType;outline(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;",
			shift = At.Shift.BEFORE
		),
		cancellable = true)
    private void frozenLib$getOutlineEasterEgg(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (glowing) {
			FrozenLibClientRegistries.ENTITY_TEXTURE_OVERRIDE.forEach(override -> {
                if (override.type() == livingEntity.getType()) {
                    var texture = override.texture();
                    if (texture != null) {
                        if (override.condition().condition(livingEntity)) {
                            cir.setReturnValue(RenderType.outline(texture));
                        }
                    }
                }
            });
        }
    }

}
