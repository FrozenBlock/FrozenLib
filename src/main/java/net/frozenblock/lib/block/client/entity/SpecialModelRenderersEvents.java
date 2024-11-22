package net.frozenblock.lib.block.client.entity;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;

@Environment(EnvType.CLIENT)
public class SpecialModelRenderersEvents {

	/**
	 * This event is called when {@link net.minecraft.client.renderer.special.SpecialModelRenderers#STATIC_BLOCK_MAPPING} is initialized and calls `put.`
	 */
	public static final Event<OnMapInit> MAP_INIT = FrozenEvents.createEnvironmentEvent(OnMapInit.class, (callbacks) -> (builder) -> {
		for (var callback : callbacks) {
			callback.onMapInit(builder);
		}
	});

	@FunctionalInterface
	public interface OnMapInit extends CommonEventEntrypoint {
		void onMapInit(ImmutableMap.Builder instance);
	}
}
