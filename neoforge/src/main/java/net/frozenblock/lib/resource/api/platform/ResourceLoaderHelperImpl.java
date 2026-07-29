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

package net.frozenblock.lib.resource.api.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.resource.PackActivationType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.resource.JarContentsPackResources;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

public final class ResourceLoaderHelperImpl {
	private static final List<ReloadListenerEntry> SERVER_LISTENERS = new ArrayList<>();
	private static final List<ReloadListenerEntry> CLIENT_LISTENERS = new ArrayList<>();
	private static final List<OrderingEntry> SERVER_ORDERINGS = new ArrayList<>();
	private static final List<OrderingEntry> CLIENT_ORDERINGS = new ArrayList<>();
	private static final List<BuiltinPackEntry> BUILTIN_PACKS = new ArrayList<>();

	public static void registerReloadListener(PackType packType, Identifier id, PreparableReloadListener listener) {
		listenersFor(packType).add(new ReloadListenerEntry(id, listener));
	}

	public static void addListenerOrdering(PackType packType, Identifier firstListener, Identifier secondListener) {
		orderingsFor(packType).add(new OrderingEntry(firstListener, secondListener));
	}

	public static boolean registerBuiltinPack(Identifier id, String modId, PackActivationType activationType) {
		return registerBuiltinPack(id, modId, null, activationType);
	}

	public static boolean registerBuiltinPack(Identifier id, String modId, @Nullable Component displayName, PackActivationType activationType) {
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
		for (ReloadListenerEntry entry : SERVER_LISTENERS) event.addListener(entry.id(), entry.listener());
		for (OrderingEntry ordering : SERVER_ORDERINGS) event.addDependency(ordering.firstListener(), ordering.secondListener());
	}

	public static void flushClientListeners(AddClientReloadListenersEvent event) {
		for (ReloadListenerEntry entry : CLIENT_LISTENERS) event.addListener(entry.id(), entry.listener());
		for (OrderingEntry ordering : CLIENT_ORDERINGS) event.addDependency(ordering.firstListener(), ordering.secondListener());
	}

	public static void flushPackFinders(AddPackFindersEvent event) {
		for (BuiltinPackEntry entry : BUILTIN_PACKS) {
			final Identifier packLocation = Identifier.fromNamespaceAndPath(entry.modId(), entry.id().getPath());
			final Component displayName = entry.displayName() != null
				? entry.displayName()
				: Component.literal(entry.modId() + "/" + entry.id().getPath());
			final boolean alwaysActive = entry.activationType() == PackActivationType.ALWAYS_ENABLED;

			try {
				addPackFinder(event, packLocation, PackType.CLIENT_RESOURCES, displayName, PackSource.DEFAULT, alwaysActive, Pack.Position.TOP);
				addPackFinder(event, packLocation, PackType.SERVER_DATA, displayName, PackSource.DEFAULT, alwaysActive, Pack.Position.TOP);
			} catch (IllegalArgumentException e) {
				FrozenLibConstants.LOGGER.warn("Failed to register builtin pack {} for mod {}", entry.id(), entry.modId(), e);
			}
		}
	}

	private static void addPackFinder(
		AddPackFindersEvent event,
		Identifier packLocation,
		PackType packType,
		Component packNameDisplay,
		PackSource packSource,
		boolean alwaysActive,
		Pack.Position packPosition
	) {
		if (event.getPackType() != packType) return;

		final IModInfo modInfo = ModList.get().getModContainerById(packLocation.getNamespace())
			.orElseThrow(() -> new IllegalArgumentException("Mod not found: " + packLocation.getNamespace()))
			.getModInfo();

		final String version = modInfo.getVersion().toString();
		final String prefix = "resourcepacks/" + packLocation.getPath();

		final Pack pack = Pack.readMetaAndCreate(
			new PackLocationInfo(
				"mod/" + packLocation,
				packNameDisplay,
				packSource,
				Optional.of(new KnownPack("neoforge", packLocation.toString(), version))
			),
			new JarContentsPackResources.JarContentsResourcesSupplier(modInfo.getOwningFile().getFile().getContents(), prefix),
			packType,
			new PackSelectionConfig(alwaysActive, packPosition, false)
		);

		if (pack == null) {
			FrozenLibConstants.LOGGER.warn("Failed to read pack metadata for builtin pack {} (looked under {})", packLocation, prefix);
			return;
		}

		event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
	}

	private record ReloadListenerEntry(Identifier id, PreparableReloadListener listener) {}
	private record OrderingEntry(Identifier firstListener, Identifier secondListener) {}
	private record BuiltinPackEntry(Identifier id, String modId, @Nullable Component displayName, PackActivationType activationType) {}
}
