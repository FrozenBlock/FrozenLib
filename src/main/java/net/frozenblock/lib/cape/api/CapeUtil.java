/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.cape.api;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class CapeUtil {
	public static @NotNull @Unmodifiable List<Cape> getCapes() {
		return ImmutableList.copyOf(FrozenRegistry.CAPE);
	}

	public static @NotNull @Unmodifiable List<Cape> getUsableCapes(UUID uuid) {
		return ImmutableList.copyOf(getCapes().stream().filter(cape -> canPlayerUserCape(uuid, cape)).toList());
	}

	public static boolean canPlayerUserCape(UUID uuid, ResourceLocation capeID) {
		Optional<Cape> optionalCape = FrozenRegistry.CAPE.getOptional(capeID);
		return optionalCape.map(cape -> canPlayerUserCape(uuid, cape)).orElse(false);
	}

	public static boolean canPlayerUserCape(UUID uuid, @NotNull Cape cape) {
		return cape.allowedPlayers().map(uuids -> uuids.contains(uuid)).orElse(true);
	}

	public static @NotNull Cape registerCape(ResourceLocation id, Component capeName) {
		return Registry.register(FrozenRegistry.CAPE, id, new Cape(id, capeName, buildCapeTextureLocation(id), Optional.empty()));
	}

	public static @NotNull Cape registerCapeWithWhitelist(ResourceLocation id, Component capeName, List<UUID> allowedPlayers) {
		return Registry.register(FrozenRegistry.CAPE, id, new Cape(id, capeName, buildCapeTextureLocation(id), Optional.of(allowedPlayers)));
	}

	public static @NotNull Cape registerCapeWithWhitelist(ResourceLocation id, Component capeName, UUID... uuids) {
		return Registry.register(FrozenRegistry.CAPE, id, new Cape(id, capeName, buildCapeTextureLocation(id), Optional.of(ImmutableList.copyOf(uuids))));
	}

	public static void registerCapesFromURL(String urlString) {
		try {
			URL url = URI.create(urlString).toURL();
			URLConnection request = url.openConnection();
			request.connect();

			JsonElement parsedJson = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
			JsonObject capeDir = parsedJson.getAsJsonObject();
			JsonArray capeArray = capeDir.get("capes").getAsJsonArray();

			capeArray.forEach(jsonElement -> {
				registerCapeFromURL(jsonElement.getAsString());
			});
		} catch (IOException ignored) {}
	}

	private static void registerCapeFromURL(String urlString) {
		try {
			URL url = URI.create(urlString).toURL();
			URLConnection request = url.openConnection();
			request.connect();

			JsonElement parsedJson = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
			JsonObject capeJson = parsedJson.getAsJsonObject();
			String capeId = capeJson.get("id").getAsString();
			Component capeName = Component.literal(capeJson.get("name").getAsString());
			String capeTexture = capeJson.get("texture").getAsString();
			JsonArray allowedUUIDs = capeJson.get("allowed_uuids").getAsJsonArray();

			ResourceLocation capeLocation = ResourceLocation.tryParse(capeId);
			if (capeLocation != null) {
				ResourceLocation capeTextureLocation = CapeUtil.buildCapeTextureLocation(capeLocation);
				if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
					ClientCapeUtil.registerCapeTextureFromURL(capeLocation, capeTextureLocation, capeTexture);
				}

				List<JsonElement> allowedUUIDList = allowedUUIDs.asList();
				if (allowedUUIDList.isEmpty()) {
					registerCape(capeLocation, capeName);
				} else {
					List<UUID> uuidList = new ArrayList<>();
					allowedUUIDList.forEach(jsonElement -> uuidList.add(UUID.fromString(jsonElement.getAsString())));
					registerCapeWithWhitelist(capeLocation, capeName, ImmutableList.copyOf(uuidList));
				}
			}
		} catch (IOException ignored) {
			FrozenSharedConstants.LOGGER.error("Failed to parse Cape from URL: {}", urlString);
		}
	}

	public static ResourceLocation buildCapeTextureLocation(@NotNull ResourceLocation cape) {
		return ResourceLocation.tryBuild(cape.getNamespace(), "textures/cape/" + cape.getPath() + ".png");
	}
}
