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

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.api.RandomPoolAliasApi;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomPoolAlias;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomPoolAlias.class)
public class RandomPoolAliasMixin {

	@Shadow
	@Final
	@Mutable
	private WeightedList<ResourceKey<StructureTemplatePool>> targets;

	// TODO: Other classes?
    @Inject(method = "<init>", at = @At("TAIL"))
    public void frozenLib$addRandomPoolAliasTargets(ResourceKey<StructureTemplatePool> alias, WeightedList<ResourceKey<StructureTemplatePool>> targets, CallbackInfo info) {
		final Identifier aliasID = alias.identifier();
		final List<Pair<Identifier, Integer>> additions = RandomPoolAliasApi.getAdditionalTargets(aliasID);

		final WeightedList.Builder<ResourceKey<StructureTemplatePool>> builder = WeightedList.builder();
		for (Weighted<ResourceKey<StructureTemplatePool>> wrapper : this.targets.unwrap()) {
			builder.add(wrapper.value(), wrapper.weight());
		}

		for (Pair<Identifier, Integer> additionalTargets : additions) {
			builder.add(ResourceKey.create(Registries.TEMPLATE_POOL, additionalTargets.getFirst()), additionalTargets.getSecond());
		}

		this.targets = builder.build();
	}

}
