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
