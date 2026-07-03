package net.frozenblock.lib.platform.service;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface EntityRendererHelper {

	<T extends Entity> void registerEntityRenderer(
		EntityType<? extends T> entityType,
		EntityRendererProvider<T> provider
	);
}
