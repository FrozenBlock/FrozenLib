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

package net.frozenblock.lib.renderer.entity.platform;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@ClientOnly
public final class EntityRendererRegistryImpl {
	private static final List<Entry<?>> ENTRIES = new ArrayList<>();

	public static <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererProvider<T> provider) {
		ENTRIES.add(new Entry<>(entityType, provider));
	}

	public static void flush(EntityRenderersEvent.RegisterRenderers event) {
		for (Entry<?> entry : ENTRIES) flushEntry(event, entry);
	}

	private static <T extends Entity> void flushEntry(EntityRenderersEvent.RegisterRenderers event, Entry<T> entry) {
		event.registerEntityRenderer(entry.entityType(), entry.provider());
	}

	private record Entry<T extends Entity>(EntityType<? extends T> entityType, EntityRendererProvider<T> provider) {}
}
