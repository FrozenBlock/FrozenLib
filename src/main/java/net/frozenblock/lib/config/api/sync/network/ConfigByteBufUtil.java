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
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.5
 */
@UtilityClass
public final class ConfigByteBufUtil {

	@Nullable
	public static <T> T readJankson(FriendlyByteBuf buf, String modId, String className) throws SyntaxError {
		try {
			final Class<T> clazz = (Class<T>) Class.forName(className);
			final Jankson jankson = ConfigSerialization.createJankson(modId);
			final JsonObject json = jankson.load(buf.readUtf());
			return jankson.fromJson(json, clazz);
		} catch (ClassNotFoundException ignored) {
		}
		return null;
	}

	public static <T> void writeJankson(FriendlyByteBuf buf, String modId, T data) {
		final Jankson jankson = ConfigSerialization.createJankson(modId);
		final JsonElement element = jankson.toJson(data);
		buf.writeUtf(element.toJson());
	}
}
