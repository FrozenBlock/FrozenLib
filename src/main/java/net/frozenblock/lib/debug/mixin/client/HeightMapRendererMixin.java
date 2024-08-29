package net.frozenblock.lib.debug.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.worldgen.heightmap.api.FrozenHeightmaps;
import net.minecraft.client.renderer.debug.HeightMapRenderer;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(HeightMapRenderer.class)
public class HeightMapRendererMixin {

	@ModifyVariable(
		method = "getColor",
		at = @At("HEAD"),
		argsOnly = true
	)
	private Heightmap.Types frozenLib$injectCustomTypes(Heightmap.Types value) {
		if (value == FrozenHeightmaps.MOTION_BLOCKING_NO_LEAVES_SYNCED) return Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
		return value;
	}

}
