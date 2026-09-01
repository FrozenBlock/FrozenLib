/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.blockentity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.frozenblock.lib.block.api.blockentity.BlockEntityTypeExtension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin<T extends BlockEntity> implements BlockEntityTypeExtension {

	@Mutable
	@Shadow
	@Final
	private Set<Block> validBlocks;

	@Unique
	private boolean frozenLib$opOnlyCustomData;

	@Unique
	@Override
	public void frozenLib$addValidBlock(Block block) {
		Objects.requireNonNull(block, "Block cannot be null");
		if (!(this.validBlocks instanceof HashSet)) this.validBlocks = new HashSet<>(this.validBlocks);
		this.validBlocks.add(block);
	}

	@Unique
	@Override
	public BlockEntityType<T> frozenLib$setOpOnlyCustomData() {
		this.frozenLib$opOnlyCustomData = true;
		return BlockEntityType.class.cast(this);
	}

	@Inject(method = "onlyOpCanSetNbt", at = @At("HEAD"), cancellable = true)
	public void frozenLib$onlyOpCanSetNbt(CallbackInfoReturnable<Boolean> info) {
		if (this.frozenLib$opOnlyCustomData) info.setReturnValue(true);
	}
}
