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

package net.frozenblock.lib.platform;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.Env;
import net.frozenblock.lib.platform.service.LoaderHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;

public class NeoLoaderHelper implements LoaderHelper {
	private static final Map<Path, FileSystem> MOD_JAR_FILESYSTEMS = new ConcurrentHashMap<>();

	private static FileSystem openOrGetFileSystem(Path jarPath) {
		return MOD_JAR_FILESYSTEMS.computeIfAbsent(jarPath, p -> {
			try {
				return FileSystems.newFileSystem(p);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	@Override
	public Path getGameDir() {
		return FMLLoader.getCurrent().getGameDir();
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public boolean isModLoaded(String modId) {
		var modList = ModList.get();
		return modList != null && modList.isLoaded(modId);
	}

	@Override
	public boolean isFabric() {
		return false;
	}

	@Override
	public boolean isNeoForge() {
		return true;
	}

	@Override
	public @Nullable <T> T ifFabric(Supplier<T> supplier) {
		return null;
	}

	@Override
	public @Nullable <T> T ifNeoForge(Supplier<T> supplier) {
		return supplier.get();
	}

	@Override
	public boolean isClient() {
		return FMLLoader.getCurrent().getDist().isClient();
	}

	@Override
	public boolean isServer() {
		return FMLLoader.getCurrent().getDist().isDedicatedServer();
	}

	@Override
	public Env getEnv() {
		return switch (FMLLoader.getCurrent().getDist()) {
			case CLIENT -> Env.CLIENT;
			case DEDICATED_SERVER -> Env.SERVER;
		};
	}

	@Override
	public Object getGameObject() {
		return isClient() ? Minecraft.getInstance() : ServerLifecycleHooks.getCurrentServer();
	}

	@Override
	public String[] getLaunchArgs() {
		return FMLLoader.getCurrent().getProgramArgs().getArguments();
	}

	@Override
	public List<ModEntry> getAllMods() {
		ModList modList = ModList.get();
		if (modList == null) return List.of();
		List<ModEntry> result = new ArrayList<>();
		for (var modInfo : modList.getMods()) {
			result.add(new ModEntry() {
				@Override
				public String getId() {
					return modInfo.getModId();
				}

				@Override
				public String getName() {
					return modInfo.getDisplayName();
				}

				@Override
				public Optional<JsonElement> getCustomData(String key) {
					return Optional.empty();
				}

				@Override
				public Optional<Path> findPath(String file) {
					var contents = modInfo.getOwningFile().getFile().getContents();
					if (!contents.containsFile(file)) return Optional.empty();
					Path primary = contents.getPrimaryPath();
					try {
						if (Files.isDirectory(primary)) {
							return Optional.of(primary.resolve(file));
						}
						FileSystem fs = openOrGetFileSystem(primary);
						Path result = fs.getPath("/" + file);
						return Files.exists(result) ? Optional.of(result) : Optional.empty();
					} catch (Exception e) {
						return Optional.empty();
					}
				}

				@Override
				public Collection<Path> getRootPaths() {
					var contents = modInfo.getOwningFile().getFile().getContents();
					Path primary = contents.getPrimaryPath();
					try {
						if (Files.isDirectory(primary)) {
							return List.of(primary);
						}
						FileSystem fs = openOrGetFileSystem(primary);
						List<Path> roots = new ArrayList<>();
						fs.getRootDirectories().forEach(roots::add);
						return List.copyOf(roots);
					} catch (Exception e) {
						return List.of();
					}
				}
			});
		}
		return result;
	}
}
