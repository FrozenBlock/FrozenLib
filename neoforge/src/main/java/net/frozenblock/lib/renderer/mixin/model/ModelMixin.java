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

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.frozenblock.lib.renderer.model.FrozenLibModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;
import java.util.function.Function;

@ClientOnly
@Mixin(Model.class)
public abstract class ModelMixin<S> implements FrozenLibModel<S> { // In common mixins.json

	@Shadow
	public abstract ModelPart root();

	@Unique
	private final Map<String, ModelPart> frozenLib$childPartMap = new Object2ObjectOpenHashMap<>();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void fillChildPartMap(ModelPart root, Function<Identifier, RenderType> renderType, CallbackInfo info) {
		this.frozenLib$calculateChildParts(root);
	}

	@Unique
	@Override
	public void frozenLib$calculateChildParts(ModelPart root) {
		root.addAllChildren(frozenLib$childPartMap::putIfAbsent);
	}

	@Unique
	@Nullable
	@Override
	public ModelPart frozenLib$getChildPart(String name) {
		return this.frozenLib$childPartMap.get(name);
	}

	@Unique
	@Override
	public void frozenLib$copyTransforms(Model<?> model) {
		frozenLib$copyTransforms(model.root(), this.root());
		model.root().addAllChildren((name, part) -> {
			final ModelPart childPart = this.frozenLib$getChildPart(name);

			if (childPart != null) frozenLib$copyTransforms(part, childPart);
		});
	}

	@Unique
	private static void frozenLib$copyTransforms(ModelPart from, ModelPart to) {
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
		to.xRot = from.xRot;
		to.yRot = from.yRot;
		to.zRot = from.zRot;
		to.xScale = from.xScale;
		to.yScale = from.yScale;
		to.zScale = from.zScale;
	}
}
