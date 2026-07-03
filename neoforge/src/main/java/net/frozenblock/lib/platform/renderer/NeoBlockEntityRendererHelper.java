package net.frozenblock.lib.platform.renderer;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.service.BlockEntityRendererHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class NeoBlockEntityRendererHelper implements BlockEntityRendererHelper {
	private record Entry<T extends BlockEntity, S extends BlockEntityRenderState>(
		BlockEntityType<? extends T> blockEntityType,
		BlockEntityRendererProvider<T, S> provider
	) {}

	private static final List<Entry<?, ?>> ENTRIES = new ArrayList<>();

	@Override
	public <T extends BlockEntity, S extends BlockEntityRenderState> void register(
		BlockEntityType<? extends T> blockEntityType,
		BlockEntityRendererProvider<T, S> provider
	) {
		ENTRIES.add(new Entry<>(blockEntityType, provider));
	}

	public static void flush(EntityRenderersEvent.RegisterRenderers event) {
		for (Entry<?, ?> entry : ENTRIES) {
			flushEntry(event, entry);
		}
	}

	private static <T extends BlockEntity, S extends BlockEntityRenderState> void flushEntry(
		EntityRenderersEvent.RegisterRenderers event,
		Entry<T, S> entry
	) {
		event.registerBlockEntityRenderer(entry.blockEntityType(), entry.provider());
	}
}
