/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.worldgen.structure.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import net.frozenblock.lib.worldgen.structure.api.TemplatePoolApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(StructureTemplatePool.class)
public class StructureTemplatePoolMixin {

	@Inject(
		method = "<init>(Lnet/minecraft/core/Holder;Ljava/util/List;)V",
		at = @At(value = "CTOR_HEAD")
	)
	public void frozenLib$appendPoolElements(
		Holder holder, List list, CallbackInfo info,
		@Local(argsOnly = true) LocalRef<List<Pair<StructurePoolElement, Integer>>> elements
	) {
		final Optional<ResourceKey<?>> optionalKey = holder.unwrapKey();
		if (optionalKey.isEmpty()) return;

		List<Pair<StructurePoolElement, Integer>> additionalElements = TemplatePoolApi.getAdditionalElements(optionalKey.get().location());
		if (additionalElements.isEmpty()) return;

		final List<Pair<StructurePoolElement, Integer>> finalElements = new ArrayList<>(elements.get());
		finalElements.addAll(additionalElements);
		elements.set(finalElements);
	}

}
