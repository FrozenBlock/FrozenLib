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

package net.frozenblock.lib.platform.attribute;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.service.DefaultAttributeRegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class NeoDefaultAttributeRegistryHelper implements DefaultAttributeRegistryHelper {
	private static final List<Entry> ENTRIES = new ArrayList<>();

	@Override
	public void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
		this.register(type, builder.build());
	}

	@Override
	public void register(EntityType<? extends LivingEntity> type, AttributeSupplier container) {
		ENTRIES.add(new Entry(type, container));
	}

	public static void flush(EntityAttributeCreationEvent event) {
		for (Entry entry : ENTRIES) event.put(entry.type(), entry.container());
	}

	private record Entry(EntityType<? extends LivingEntity> type, AttributeSupplier container) {}
}
