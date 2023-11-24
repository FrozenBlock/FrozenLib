package net.frozenblock.lib.config.api.network;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigByteBufUtil {

	public static <T> T readJankson(FriendlyByteBuf buf, String modId, String className) throws SyntaxError, ClassNotFoundException {
		Jankson jankson = ConfigSerialization.createJankson(modId);
		Class<T> clazz = (Class<T>) Class.forName(className);
		JsonObject json = jankson.load(buf.readUtf());
		return jankson.fromJson(json, clazz);
	}

	public static <T> void writeJankson(FriendlyByteBuf buf, String modId, T data) {
		Jankson jankson = ConfigSerialization.createJankson(modId);
		JsonElement element = jankson.toJson(data);
		buf.writeUtf(element.toJson());
	}
}
