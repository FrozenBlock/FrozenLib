package net.frozenblock.lib.platform.renderer;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.service.EntityRendererHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class NeoEntityRendererHelper implements EntityRendererHelper {
	private record Entry<T extends Entity>(EntityType<? extends T> entityType, EntityRendererProvider<T> provider) {}

	private static final List<Entry<?>> ENTRIES = new ArrayList<>();

	@Override
	public <T extends Entity> void registerEntityRenderer(
		EntityType<? extends T> entityType,
		EntityRendererProvider<T> provider
	) {
		ENTRIES.add(new Entry<>(entityType, provider));
	}

	public static void flush(EntityRenderersEvent.RegisterRenderers event) {
		for (Entry<?> entry : ENTRIES) {
			flushEntry(event, entry);
		}
	}

	private static <T extends Entity> void flushEntry(EntityRenderersEvent.RegisterRenderers event, Entry<T> entry) {
		event.registerEntityRenderer(entry.entityType(), entry.provider());
	}
}
