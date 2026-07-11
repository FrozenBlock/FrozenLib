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

package net.frozenblock.lib.platform.client.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Environment(EnvType.CLIENT)
public final class ModelLayerRegistryImpl {
	private static final List<Entry> LAYERS = new ArrayList<>();

	public static void register(ModelLayerLocation layer, Supplier<LayerDefinition> provider) {
		LAYERS.add(new Entry(layer, provider));
	}

	public static void flush(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (Entry entry : LAYERS) event.registerLayerDefinition(entry.layer(), entry.provider());
	}

	private record Entry(ModelLayerLocation layer, Supplier<LayerDefinition> provider) {}
}
