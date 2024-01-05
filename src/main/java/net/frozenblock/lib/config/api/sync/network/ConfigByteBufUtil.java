/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.api.sync.network;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.5
 */
public final class ConfigByteBufUtil {
	private ConfigByteBufUtil() {}

	public static <T> T readJankson(@NotNull FriendlyByteBuf buf, String modId, String className) throws SyntaxError, ClassNotFoundException {
		Jankson jankson = ConfigSerialization.createJankson(modId);
		Class<T> clazz = (Class<T>) Class.forName(className);
		JsonObject json = jankson.load(buf.readUtf());
		return jankson.fromJson(json, clazz);
	}

	public static <T> void writeJankson(@NotNull FriendlyByteBuf buf, String modId, T data) {
		Jankson jankson = ConfigSerialization.createJankson(modId);
		JsonElement element = jankson.toJson(data);
		buf.writeUtf(element.toJson());
	}
}
