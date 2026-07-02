package net.frozenblock.lib.platform.resource;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.resource.PackActivationType;
import net.frozenblock.lib.platform.service.ResourceLoaderHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.jetbrains.annotations.Nullable;

public class NeoResourceLoaderHelper implements ResourceLoaderHelper {
	private record ReloadListenerEntry(Identifier id, PreparableReloadListener listener) {}
	private record OrderingEntry(Identifier firstListener, Identifier secondListener) {}
	private record BuiltinPackEntry(Identifier id, String modId, @Nullable Component displayName, PackActivationType activationType) {}

	private static final List<ReloadListenerEntry> SERVER_LISTENERS = new ArrayList<>();
	private static final List<ReloadListenerEntry> CLIENT_LISTENERS = new ArrayList<>();
	private static final List<OrderingEntry> SERVER_ORDERINGS = new ArrayList<>();
	private static final List<OrderingEntry> CLIENT_ORDERINGS = new ArrayList<>();
	private static final List<BuiltinPackEntry> BUILTIN_PACKS = new ArrayList<>();

	@Override
	public void registerReloadListener(PackType packType, Identifier id, PreparableReloadListener listener) {
		listenersFor(packType).add(new ReloadListenerEntry(id, listener));
	}

	@Override
	public void addListenerOrdering(PackType packType, Identifier firstListener, Identifier secondListener) {
		orderingsFor(packType).add(new OrderingEntry(firstListener, secondListener));
	}

	@Override
	public boolean registerBuiltinPack(Identifier id, String modId, PackActivationType activationType) {
		return registerBuiltinPack(id, modId, null, activationType);
	}

	@Override
	public boolean registerBuiltinPack(Identifier id, String modId, @Nullable Component displayName, PackActivationType activationType) {
		ModList modList = ModList.get();
		if (modList != null && modList.getModContainerById(modId).isEmpty()) return false;
		BUILTIN_PACKS.add(new BuiltinPackEntry(id, modId, displayName, activationType));
		return true;
	}

	private static List<ReloadListenerEntry> listenersFor(PackType packType) {
		return packType == PackType.CLIENT_RESOURCES ? CLIENT_LISTENERS : SERVER_LISTENERS;
	}

	private static List<OrderingEntry> orderingsFor(PackType packType) {
		return packType == PackType.CLIENT_RESOURCES ? CLIENT_ORDERINGS : SERVER_ORDERINGS;
	}

	public static void flushServerListeners(AddServerReloadListenersEvent event) {
		for (ReloadListenerEntry entry : SERVER_LISTENERS) {
			event.addListener(entry.id(), entry.listener());
		}
		for (OrderingEntry ordering : SERVER_ORDERINGS) {
			event.addDependency(ordering.firstListener(), ordering.secondListener());
		}
	}

	public static void flushClientListeners(AddClientReloadListenersEvent event) {
		for (ReloadListenerEntry entry : CLIENT_LISTENERS) {
			event.addListener(entry.id(), entry.listener());
		}
		for (OrderingEntry ordering : CLIENT_ORDERINGS) {
			event.addDependency(ordering.firstListener(), ordering.secondListener());
		}
	}

	public static void flushPackFinders(AddPackFindersEvent event) {
		for (BuiltinPackEntry entry : BUILTIN_PACKS) {
			Identifier packLocation = Identifier.fromNamespaceAndPath(entry.modId(), entry.id().getPath());
			Component displayName = entry.displayName() != null
				? entry.displayName()
				: Component.literal(entry.modId() + "/" + entry.id().getPath());
			boolean alwaysActive = entry.activationType() == PackActivationType.ALWAYS_ENABLED;

			try {
				event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, displayName, PackSource.DEFAULT, alwaysActive, Pack.Position.TOP);
				event.addPackFinders(packLocation, PackType.SERVER_DATA, displayName, PackSource.DEFAULT, alwaysActive, Pack.Position.TOP);
			} catch (IllegalArgumentException e) {
				FrozenLibConstants.LOGGER.warn("Failed to register builtin pack {} for mod {}", entry.id(), entry.modId(), e);
			}
		}
	}
}
