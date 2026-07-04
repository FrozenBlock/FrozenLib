package net.frozenblock.lib.block.mixin.blockentity;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collections;
import java.util.Set;
import net.frozenblock.lib.block.api.blockentity.BlockEntityTypeExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin implements BlockEntityTypeExtension {

	@Shadow
	@Final
	private Set<Block> validBlocks;

	@Override
	public void frozenLib$addValidBlock(Block block) {
		this.validBlocks.add(block);
	}
}
