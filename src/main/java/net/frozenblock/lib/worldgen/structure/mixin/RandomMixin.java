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

package net.frozenblock.lib.worldgen.structure.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.api.RandomPoolAliasApi;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Random.class)
public class RandomMixin {

    @Shadow
	@Final
	@Mutable
	private SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> targets;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void frozenLib$addRandomPoolAliasTargets(ResourceKey<StructureTemplatePool> alias, SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> targets, CallbackInfo info) {
        ResourceLocation aliasLocation = alias.location();
		List<Pair<ResourceLocation, Integer>> additions = RandomPoolAliasApi.getAdditionalTargets(aliasLocation);

		SimpleWeightedRandomList.Builder<ResourceKey<StructureTemplatePool>> builder = SimpleWeightedRandomList.builder();

		for (WeightedEntry.Wrapper<ResourceKey<StructureTemplatePool>> wrapper : this.targets.unwrap()) {
			builder.add(wrapper.data(), wrapper.weight().asInt());
		}

		for (Pair<ResourceLocation, Integer> additionalTargets : additions) {
			builder.add(ResourceKey.create(Registries.TEMPLATE_POOL, additionalTargets.getFirst()), additionalTargets.getSecond());
		}

		this.targets = builder.build();
    }

}
