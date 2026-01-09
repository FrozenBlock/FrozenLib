/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.spotting_icons.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.spotting_icons.impl.client.EntityRenderStateWithIcon;
import net.frozenblock.lib.spotting_icons.impl.client.SpottingIconRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderState.class)
public class EntityRenderStateInterface implements EntityRenderStateWithIcon {

	@Unique
	private final SpottingIconRenderState frozenLib$spottingIconRenderState = new SpottingIconRenderState();

	@Override
	public SpottingIconRenderState frozenLib$getIconRenderState() {
		return this.frozenLib$spottingIconRenderState;
	}
}
