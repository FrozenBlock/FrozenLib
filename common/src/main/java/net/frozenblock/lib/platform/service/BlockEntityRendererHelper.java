package net.frozenblock.lib.platform.service;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BlockEntityRendererHelper {

	<T extends BlockEntity, S extends BlockEntityRenderState> void register(
		BlockEntityType<? extends T> blockEntityType,
		BlockEntityRendererProvider<T, S> provider
	);
}
