/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
