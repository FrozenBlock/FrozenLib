package net.frozenblock.lib.platform.renderer;

import net.frozenblock.lib.platform.service.BlockEntityRendererHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricBlockEntityRendererHelper implements BlockEntityRendererHelper {

	@Override
	public <T extends BlockEntity, S extends BlockEntityRenderState> void register(
		BlockEntityType<? extends T> blockEntityType,
		BlockEntityRendererProvider<T, S> provider
	) {
		BlockEntityRenderers.register(blockEntityType, provider);
	}
}
