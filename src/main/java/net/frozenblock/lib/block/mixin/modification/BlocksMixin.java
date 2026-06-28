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

package net.frozenblock.lib.block.mixin.modification;

import java.util.function.Function;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.block.api.modification.BlockRegistryModificationEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blocks.class)
public final class BlocksMixin {

	@Inject(
		method = "register(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;",
		at = @At("HEAD")
	)
	private static void frozenLib$modifyPropertiesAndReplaceFactory(
		CallbackInfoReturnable<Block> info,
		@Local(argsOnly = true) ResourceKey<Block> id,
		@Local(argsOnly = true) LocalRef<Function<BlockBehaviour.Properties, Block>> factory,
		@Local(argsOnly = true) LocalRef<BlockBehaviour.Properties> properties
	) {
		properties.set(BlockRegistryModificationEvents.MODIFY_PROPERTIES.invoker().modifyProperties(id, properties.get()));
		factory.set(BlockRegistryModificationEvents.REPLACE_FACTORY.invoker().replaceFactory(id, properties.get(), factory.get()));
	}
}
