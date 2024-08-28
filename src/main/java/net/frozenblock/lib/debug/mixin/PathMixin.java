/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.debug.mixin;

import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Path.class)
public class PathMixin {
	@Shadow @Nullable
	private Path.@Nullable DebugData debugData;
	@Shadow @Final
	private List<Node> nodes;
	@Shadow @Final
	private BlockPos target;

	@Inject(method = "writeToStream", at = @At("HEAD"))
	private void frozenLib$writeToStream(FriendlyByteBuf buf, CallbackInfo ci) {
		this.debugData = new Path.DebugData(
			this.nodes.stream().filter((pathNode) -> !pathNode.closed).toArray(Node[]::new),
			this.nodes.stream().filter((pathNode) -> pathNode.closed).toArray(Node[]::new),
			Set.of(
				new Target(
					this.target.getX(),
					this.target.getY(),
					this.target.getZ()
				)
			)
		);
	}
}
