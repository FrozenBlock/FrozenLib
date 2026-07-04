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

package net.frozenblock.lib.entity.api.attribute;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

@UtilityClass
public class FrozenDefaultAttributeRegistry {

	public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
		FrozenLibInitPlatformUtils.DEFAULT_ATTRIBUTE_REGISTRY.register(type, builder);
	}

	public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier container) {
		FrozenLibInitPlatformUtils.DEFAULT_ATTRIBUTE_REGISTRY.register(type, container);
	}
}
