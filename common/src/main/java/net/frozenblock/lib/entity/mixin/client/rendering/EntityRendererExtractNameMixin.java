package net.frozenblock.lib.entity.mixin.client.rendering;

import net.frozenblock.lib.platform.api.ClientOnly;
import net.frozenblock.lib.renderer.FrozenLibRenderStateDataKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(EntityRenderer.class)
public class EntityRendererExtractNameMixin<T extends Entity, S extends EntityRenderState> {

	@Inject(
		method = "extractRenderState",
		at = @At("TAIL")
	)
	private void frozenLib$storeEntityName(T entity, S state, float partialTicks, CallbackInfo ci) {
		if (entity != null) {
			final String name = ChatFormatting.stripFormatting(entity.getDisplayName().getString());
			state.frozenLib$setData(FrozenLibRenderStateDataKeys.ENTITY_NAME, name);
		}
	}

}
