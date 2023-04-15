/*
 * Copyright 2023 FrozenBlock
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
