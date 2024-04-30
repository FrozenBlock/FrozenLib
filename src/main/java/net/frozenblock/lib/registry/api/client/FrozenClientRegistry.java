/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.registry.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.entity.api.rendering.EntityTextureOverride;
import net.minecraft.core.MappedRegistry;

@Environment(EnvType.CLIENT)
public class FrozenClientRegistry {

	public static final MappedRegistry<EntityTextureOverride> ENTITY_TEXTURE_OVERRIDE = FabricRegistryBuilder.createSimple(EntityTextureOverride.class, FrozenSharedConstants.id("entity_texture_override"))
			.buildAndRegister();

	public static void initRegistry() {
		// NO-OP
		// This is just to make sure the registry is initialized.
	}
}
