package net.frozenblock.lib.platform.renderer;

import net.frozenblock.lib.platform.service.EntityRendererHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class FabricEntityRendererHelper implements EntityRendererHelper {

	@Override
	public <T extends Entity> void registerEntityRenderer(
		EntityType<? extends T> entityType,
		EntityRendererProvider<T> provider
	) {
		EntityRenderers.register(entityType, provider);
	}
}
