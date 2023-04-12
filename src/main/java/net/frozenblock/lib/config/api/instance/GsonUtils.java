package net.frozenblock.lib.config.api.instance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.json.ColorSerializer;
import net.frozenblock.lib.config.api.instance.json.TypedEntrySerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import java.awt.*;

public class GsonUtils {
	private GsonUtils() {
	}

	public static Gson createGson(GsonBuilder builder, String modId) {
		return builder
			.registerTypeHierarchyAdapter(TypedEntry.class, new TypedEntrySerializer<>(modId))
			.registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
			.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
			.registerTypeHierarchyAdapter(Color.class, new ColorSerializer())
			.serializeNulls()
			.setPrettyPrinting()
			.create();
	}

	public static Gson createGson(String modId) {
		return createGson(new GsonBuilder(), modId);
	}
}
