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
