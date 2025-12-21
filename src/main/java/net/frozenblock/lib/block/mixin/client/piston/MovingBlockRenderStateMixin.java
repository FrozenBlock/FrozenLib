/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.mixin.client.piston;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.impl.state.MovingBlockRenderStateImpl;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(MovingBlockRenderState.class)
public class MovingBlockRenderStateMixin implements MovingBlockRenderStateImpl {

	@Unique
	private BlockEntityRenderState frozenLib$blockEntityRenderState = null;

	@Override
	public void frozenLib$setBlockEntityRenderState(BlockEntityRenderState renderState) {
		this.frozenLib$blockEntityRenderState = renderState;
	}

	@Override
	public BlockEntityRenderState frozenLib$getBlockEntityRenderState() {
		return this.frozenLib$blockEntityRenderState;
	}
}
