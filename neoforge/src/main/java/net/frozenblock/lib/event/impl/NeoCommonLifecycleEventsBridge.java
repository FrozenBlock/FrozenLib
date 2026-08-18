package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.DefaultDataComponentsBoundEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@UtilityClass
public class NeoCommonLifecycleEventsBridge {
	private static RegistryAccess pendingRegistries;
	private static boolean pendingClient;

	public static void init() {
		NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.ServerDataLoad.class, event -> {
			pendingRegistries = event.getRegistries();
			pendingClient = false;
		});
		NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.ClientPacketReceived.class, event -> {
			pendingRegistries = event.getRegistries();
			pendingClient = true;
		});
		NeoForge.EVENT_BUS.addListener(DefaultDataComponentsBoundEvent.class, event -> {
			if (pendingRegistries == null) return;
			CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(pendingRegistries, pendingClient);
			pendingRegistries = null;
		});
	}
}
