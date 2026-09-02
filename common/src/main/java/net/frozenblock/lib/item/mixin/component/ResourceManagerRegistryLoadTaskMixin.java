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

package net.frozenblock.lib.item.mixin.component;

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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.frozenblock.lib.item.api.component.BlockTransformerEvents;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: make sure neo doesn't ALSO change this
// TODO: possibly make an api for impl such as this once neo functionality is tested
@Mixin(ResourceManagerRegistryLoadTask.class)
public class ResourceManagerRegistryLoadTaskMixin {
	@Unique
	private volatile RegistryOps.RegistryInfoLookup frozenLib$fabric$registryInfoLookup;

	@WrapOperation(
		method = "lambda$load$2",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/datafixers/util/Either;Lnet/minecraft/core/RegistrationInfo;)Lnet/minecraft/resources/RegistryLoadTask$PendingRegistration;"
		)
	)
	private <T> RegistryLoadTask.PendingRegistration<?> frozenLib$fabric$modifyBlockTransformers(
		ResourceKey<T> key,
		Either<T, Exception> value,
		RegistrationInfo registrationInfo,
		Operation<RegistryLoadTask.PendingRegistration<T>> original,
		@Local(argsOnly = true) Resource thunk
	) {
		// (FrozenLib) Implementation is modified to not include ResourceSource and to (currently) only apply to BlockTransformers.
		// (FrozenLib) Implementation is also modified to invoke our custom BlockTransformer modification events based on the key being used.
		if (value.left().isPresent()) {
			T leftValue = value.left().get();
			T modified = null;

			if (leftValue instanceof BlockTransformer blockTransformer) {
				final BlockTransformerEvents.Context context = BlockTransformerEvents.createContext(blockTransformer);

				if (key.equals(BlockTransformers.AXE)) {
					final BlockTransformerEvents.AxeStrippablesContext axeStrippablesContext = BlockTransformerEvents.createAxeStrippablesContext();
					BlockTransformerEvents.ADD_AXE_STRIPPABLE.invoker().addAxeStrippables(
						axeStrippablesContext,
						(fromBlock, toBlock) -> axeStrippablesContext.addLast(BlockPredicate.matchesBlocks(fromBlock), new CopyPropertiesProvider(toBlock)),
						this.frozenLib$fabric$registryInfoLookup
					);
					if (axeStrippablesContext.modified()) context.addFirst(axeStrippablesContext.toBlockTransformData());

					BlockTransformerEvents.MODIFY_AXE.invoker().modifyAxeBlockTransformer(context, this.frozenLib$fabric$registryInfoLookup);
				} else if (key.equals(BlockTransformers.HOE)) {
					BlockTransformerEvents.MODIFY_HOE.invoker().modifyHoeBlockTransformer(context, this.frozenLib$fabric$registryInfoLookup);
				} else if (key.equals(BlockTransformers.SHOVEL)) {
					BlockTransformerEvents.MODIFY_SHOVEL.invoker().modifyShovelBlockTransformer(context, this.frozenLib$fabric$registryInfoLookup);
				}
				BlockTransformerEvents.MODIFY.invoker().modifyBlockTransformer((ResourceKey<BlockTransformer>) key, context, this.frozenLib$fabric$registryInfoLookup);

				if (context.modified()) modified = (T) context.toBlockTransformer();
			}

			if (modified != null) {
				// Clear the knownPackInfo to force the server to sync the data pack to the client
				registrationInfo = new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle());
				value = Either.left(modified);
			}
		}

		return original.call(key, value, registrationInfo);
	}

	@Inject(method = "load", at = @At("HEAD"))
	private void frozenLib$fabric$captureRegistries(RegistryOps.RegistryInfoLookup context, Executor executor, CallbackInfoReturnable<CompletableFuture<?>> info) {
		this.frozenLib$fabric$registryInfoLookup = context;
	}
}
