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

package net.frozenblock.lib.config.newconfig.impl.network;

import net.frozenblock.lib.config.api.instance.xjs.NonSerializableObjectException;
import net.frozenblock.lib.config.api.instance.xjs.XjsObjectMapper;
import net.frozenblock.lib.config.newconfig.registry.ID;
import net.minecraft.network.FriendlyByteBuf;
import xjs.data.JsonObject;
import xjs.data.serialization.JsonContext;

public class ConfigEntryByteBufUtil {

	public static <T> T readUbjson(FriendlyByteBuf buf, ID entryId, String className) throws Exception {
		final Class<T> clazz = (Class<T>) Class.forName(className);
		return XjsObjectMapper.deserializeObject(
			null,
			(xjs.data.JsonObject) JsonContext.getParser("ubjson").parse(buf.readUtf()),
			(Class<T>) Class.forName(className)
		);
	}

	public static <T> void writeUbjson(FriendlyByteBuf buf, ID entryId, T data) throws NonSerializableObjectException {
		final JsonObject json = XjsObjectMapper.toJsonObject(data);
		buf.writeUtf(json.toString("ubjson"));
	}
}
