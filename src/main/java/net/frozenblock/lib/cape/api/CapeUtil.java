/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

public class CapeUtil {
	@ApiStatus.Internal
	private static final Map<Identifier, Cape> CAPES = new HashMap<>();
	@ApiStatus.Internal
	private static final List<String> CAPE_REPOS = new ArrayList<>();

	@Unmodifiable
	public static List<String> getCapeRepos() {
		return ImmutableList.copyOf(CAPE_REPOS);
	}

	@Unmodifiable
	public static Collection<Cape> getCapes() {
		return CAPES.values();
	}

	@Unmodifiable
	public static List<Cape> getUsableCapes(UUID uuid) {
		return ImmutableList.copyOf(getCapes().stream().filter(cape -> canPlayerUserCape(uuid, cape)).toList());
	}

	public static Optional<Cape> getCape(Identifier capeID) {
		return Optional.ofNullable(CAPES.get(capeID));
	}

	public static boolean canPlayerUserCape(UUID uuid, Identifier capeID) {
		final Optional<Cape> optionalCape = CapeUtil.getCape(capeID);
		return optionalCape.map(cape -> canPlayerUserCape(uuid, cape)).orElse(true);
	}

	public static boolean canPlayerUserCape(UUID uuid, Cape cape) {
		return cape.allowedPlayers().map(uuids -> uuids.contains(uuid)).orElse(true);
	}

	public static void registerCape(Identifier id, Identifier texture, Component name) {
		CAPES.put(id, new Cape(id, name, texture, Optional.empty()));
	}

	public static void registerCape(Identifier id, Component name) {
		CAPES.put(id, new Cape(id, name, buildCapeTextureLocation(id), Optional.empty()));
	}

	public static void registerCapeWithWhitelist(Identifier id, Component name, List<UUID> allowedPlayers) {
		CAPES.put(id, new Cape(id, name, buildCapeTextureLocation(id), Optional.of(allowedPlayers)));
	}

	public static void registerCapeWithWhitelist(Identifier id, Component name, UUID... uuids) {
		CAPES.put(id, new Cape(id, name, buildCapeTextureLocation(id), Optional.of(ImmutableList.copyOf(uuids))));
	}

	public static void registerCapesFromURL(String urlString) {
		if (CAPE_REPOS.contains(urlString)) return;

		CompletableFuture.supplyAsync(
			() -> {
				try {
					final URL url = URI.create(urlString).toURL();
					final URLConnection request = url.openConnection();
					request.connect();

					final JsonElement parsedJson = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
					final JsonObject capeDir = parsedJson.getAsJsonObject();
					final JsonArray capeArray = capeDir.get("capes").getAsJsonArray();

					capeArray.forEach(jsonElement -> registerCapeFromURL(jsonElement.getAsString()));
					return Optional.of(urlString);
				} catch (IOException ignored) {}
				return Optional.empty();
			},
			Executors.newCachedThreadPool()
		).whenComplete((value, throwable) -> {
			value.ifPresent(string -> CAPE_REPOS.add((String) string));
		});
	}

	private static void registerCapeFromURL(String urlString) {
		try {
			final URL url = URI.create(urlString).toURL();
			final URLConnection request = url.openConnection();
			request.connect();

			final JsonElement parsedJson = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
			final JsonObject capeJson = parsedJson.getAsJsonObject();
			final String capeId = capeJson.get("id").getAsString();
			final Component capeName = Component.literal(capeJson.get("name").getAsString());
			final String capeTexture = capeJson.get("texture").getAsString();
			final JsonElement allowedUUIDElement = capeJson.get("allowed_uuids");
			final boolean whitelisted = allowedUUIDElement != null;

			final Identifier capeLocation = Identifier.tryParse(capeId);
			if (capeLocation == null) return;

			final Identifier capeTextureLocation = CapeUtil.buildCapeTextureLocation(capeLocation);
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ClientCapeUtil.registerCapeTextureFromURL(capeLocation, capeTextureLocation, capeTexture);

			if (!whitelisted) {
				registerCape(capeLocation, capeName);
			} else {
				final List<UUID> uuidList = new ArrayList<>();
				allowedUUIDElement.getAsJsonArray().asList().forEach(jsonElement -> uuidList.add(UUID.fromString(jsonElement.getAsString())));
				registerCapeWithWhitelist(capeLocation, capeName, ImmutableList.copyOf(uuidList));
			}
		} catch (IOException ignored) {
			FrozenLibConstants.LOGGER.error("Failed to parse Cape from URL: {}", urlString);
		}
	}

	public static Identifier buildCapeTextureLocation(Identifier cape) {
		return Identifier.tryBuild(cape.getNamespace(), "textures/cape/" + cape.getPath() + ".png");
	}
}
