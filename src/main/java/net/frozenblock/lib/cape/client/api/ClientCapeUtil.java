/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.cape.client.api;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

@Environment(EnvType.CLIENT)
public class ClientCapeUtil {
	@ApiStatus.Internal
	public static final Path CAPE_CACHE_PATH = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("cape_cache");
	@ApiStatus.Internal
	private static final List<Identifier> REGISTERED_CAPE_LISTENERS = new ArrayList<>();
	@ApiStatus.Internal
	private static final List<Cape> USABLE_CAPES = new ArrayList<>();

	public static void registerCapeTextureFromURL(Identifier capeID, Identifier texture, String textureURL) throws JsonIOException {
		if (REGISTERED_CAPE_LISTENERS.contains(capeID)) return;

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return capeID;
			}

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				Minecraft.getInstance().getSkinManager().skinTextureDownloader.downloadAndRegisterSkin(
					texture,
					CAPE_CACHE_PATH.resolve(capeID.getNamespace()).resolve(capeID.getPath() + ".png"),
					textureURL,
					false
				);
			}
		});
		REGISTERED_CAPE_LISTENERS.add(capeID);
	}

	public static void refreshUsableCapes() {
		USABLE_CAPES.clear();
		final UUID playerUUID = Minecraft.getInstance().getUser().getProfileId();
		USABLE_CAPES.addAll(CapeUtil.getUsableCapes(playerUUID));
	}

	public static @Unmodifiable List<Cape> getUsableCapes(boolean refresh) {
		if (refresh) refreshUsableCapes();
		return ImmutableList.copyOf(USABLE_CAPES);
	}

	public static boolean hasUsableCapes(boolean refresh) {
		if (refresh) refreshUsableCapes();
		return USABLE_CAPES.size() > 1;
	}
}
