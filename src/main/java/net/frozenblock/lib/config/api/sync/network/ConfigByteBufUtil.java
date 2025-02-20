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

package net.frozenblock.lib.config.api.sync.network;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.frozenblock.lib.config.api.instance.xjs.XjsObjectMapper;
import net.frozenblock.lib.config.api.instance.xjs.XjsUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import xjs.compat.serialization.util.UBTyping;
import xjs.compat.serialization.writer.UbjsonWriter;
import java.io.InputStream;
import java.util.Optional;

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

	public static <T> T readXjs(@NotNull FriendlyByteBuf buf, String modId, String className) throws Exception {
		try (InputStream stream = new ByteBufInputStream(buf)) {
			Optional<xjs.data.JsonObject> xjs = XjsUtils.readJson(stream);
			Class<T> clazz = (Class<T>) Class.forName(className);
			return XjsObjectMapper.deserializeObject(modId, xjs, clazz);
		}
	}

	public static <T> void writeXjs(@NotNull FriendlyByteBuf buf, T data) throws Exception {
		xjs.data.JsonObject xjs = XjsObjectMapper.toJsonObject(data);
		try (UbjsonWriter writer = new UbjsonWriter(new ByteBufOutputStream(buf), UBTyping.COMPRESSED)) {
			writer.write(xjs);
		}
	}
}
