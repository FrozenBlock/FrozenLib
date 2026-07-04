package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.ClientEntityLifecycleEvents;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@UtilityClass
public class NeoEntityLifecycleEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class, NeoEntityLifecycleEventBridge::onJoin);
		NeoForge.EVENT_BUS.addListener(EntityLeaveLevelEvent.class, NeoEntityLifecycleEventBridge::onLeave);
	}

	private static void onJoin(EntityJoinLevelEvent event) {
		final Level level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			EntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(event.getEntity(), serverLevel);
		} else if (level instanceof ClientLevel clientLevel) {
			ClientEntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(event.getEntity(), clientLevel);
		}
	}

	private static void onLeave(EntityLeaveLevelEvent event) {
		final Level level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			EntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(event.getEntity(), serverLevel);
		} else if (level instanceof ClientLevel clientLevel) {
			ClientEntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(event.getEntity(), clientLevel);
		}
	}
}
