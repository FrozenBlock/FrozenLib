package net.frozenblock.lib.entrypoint.impl.platform;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.entrypoint.EntrypointStorage;
import net.fabricmc.loader.impl.metadata.EntrypointMetadata;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entrypoint.impl.FrozenLibEntrypoints;

public final class FrozenLibEntrypointInjectorImpl {

	public static void inject() {
		EntrypointStorage storage;
		Map<String, LanguageAdapter> adapterMap;
		try {
			FabricLoaderImpl loader = FabricLoaderImpl.INSTANCE;

			Field storageField = FabricLoaderImpl.class.getDeclaredField("entrypointStorage");
			storageField.setAccessible(true);
			storage = (EntrypointStorage) storageField.get(loader);

			Field adapterField = FabricLoaderImpl.class.getDeclaredField("adapterMap");
			adapterField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, LanguageAdapter> castAdapterMap = (Map<String, LanguageAdapter>) adapterField.get(loader);
			adapterMap = castAdapterMap;
		} catch (ReflectiveOperationException | ClassCastException e) {
			FrozenLibConstants.LOGGER.error(
				"Failed to hook into Fabric Loader's entrypoint storage - frozenlib.json entrypoints will "
					+ "only be visible through EntrypointHelper, not FabricLoader#getEntrypoints", e
			);
			return;
		}

		for (String key : FrozenLibEntrypoints.getKeys()) {
			for (FrozenLibEntrypoints.DeclaredEntrypoint declared : FrozenLibEntrypoints.getDeclared(key)) {
				Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(declared.modId());
				if (container.isEmpty() || !(container.get() instanceof ModContainerImpl modContainer)) continue;

				try {
					storage.add(modContainer, key, new EntrypointMetadata() {
						@Override
						public String getAdapter() {
							return "default";
						}

						@Override
						public String getValue() {
							return declared.className();
						}
					}, adapterMap);
				} catch (Exception e) {
					FrozenLibConstants.LOGGER.error(
						"Failed to inject frozenlib.json entrypoint '{}' (key '{}') into Fabric Loader", declared.className(), key, e
					);
				}
			}
		}

		FrozenLibEntrypoints.markInjectedIntoNativeLoader();
	}

	private FrozenLibEntrypointInjectorImpl() {}
}
