package net.frozenblock.lib.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.frozenblock.lib.block.impl.fire.FireType;

@Environment(EnvType.CLIENT)
public class FrozenLibRenderStateDataKeys {
	public static final RenderStateDataKey<FireType> FIRE_TYPE = RenderStateDataKey.create();
}
