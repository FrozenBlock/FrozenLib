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

package net.frozenblock.lib.renderer.mixin.model;

import net.fabricmc.fabric.impl.client.rendering.ModelExtensions;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.frozenblock.lib.renderer.model.FrozenLibModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@ClientOnly
@Mixin(Model.class)
public class ModelMixin implements FrozenLibModel { // In common mixins.json

	@Unique
	@Override
	public void frozenLib$calculateChildParts(ModelPart root) {
		if (Model.class.cast(this) instanceof ModelExtensions modelExtensions) modelExtensions.fabric$calculateChildParts(root);
	}

	@Unique
	@Nullable
	@Override
	public ModelPart frozenLib$getChildPart(String name) {
		return Model.class.cast(this).getChildPart(name);
	}

	@Unique
	@Override
	public void frozenLib$copyTransforms(Model<?> model) {
		Model.class.cast(this).copyTransforms(model);
	}
}
