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

package net.frozenblock.lib.renderer.special.platform;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

public final class SpecialModelRendererRegistryImpl {
	private static final List<Entry> ENTRIES = new ArrayList<>();

	public static void register(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> rendererCodec) {
		ENTRIES.add(new Entry(id, rendererCodec));
	}

	public static void flush(RegisterSpecialModelRendererEvent event) {
		for (Entry entry : ENTRIES) flushEntry(event, entry);
	}

	private static void flushEntry(RegisterSpecialModelRendererEvent event, Entry entry) {
		event.register(entry.id, entry.rendererCodec);
	}

	private record Entry(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> rendererCodec) {}
}
