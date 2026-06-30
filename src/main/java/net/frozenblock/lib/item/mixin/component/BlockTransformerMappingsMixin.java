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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.item.api.component.BlockTransformerMappingsApi;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.world.item.component.BlockTransformerMappings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockTransformerMappings.class)
public class BlockTransformerMappingsMixin {

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "NEW",
			target = "(Ljava/util/List;)Lnet/minecraft/core/component/BlockTransformer;",
			ordinal = 0
		),
		slice = @Slice(
			to = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/item/component/BlockTransformerMappings;SHOVEL:Lnet/minecraft/core/component/BlockTransformer;",
				opcode = Opcodes.PUTSTATIC
			)
		)
	)
	private static BlockTransformer frozenLib$modifyShovelBlockTransformers(BlockTransformer original) {
		final BlockTransformerMappingsApi.Context context = BlockTransformerMappingsApi.createContext(original);
		BlockTransformerMappingsApi.MODIFY_SHOVEL.invoker().modifyShovelBlockTransformer(context);
		return context.toBlockTransformer();
	}

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "NEW",
			target = "(Ljava/util/List;)Lnet/minecraft/core/component/BlockTransformer;",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/item/component/BlockTransformerMappings;SHOVEL:Lnet/minecraft/core/component/BlockTransformer;",
				opcode = Opcodes.PUTSTATIC
			),
			to = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/item/component/BlockTransformerMappings;AXE:Lnet/minecraft/core/component/BlockTransformer;",
				opcode = Opcodes.PUTSTATIC
			)
		)
	)
	private static BlockTransformer frozenLib$modifyAxeBlockTransformers(BlockTransformer original) {
		final BlockTransformerMappingsApi.Context context = BlockTransformerMappingsApi.createContext(original);
		BlockTransformerMappingsApi.MODIFY_AXE.invoker().modifyAxeBlockTransformer(context);
		return context.toBlockTransformer();
	}

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "NEW",
			target = "(Ljava/util/List;)Lnet/minecraft/core/component/BlockTransformer;",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/item/component/BlockTransformerMappings;AXE:Lnet/minecraft/core/component/BlockTransformer;",
				opcode = Opcodes.PUTSTATIC
			),
			to = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/item/component/BlockTransformerMappings;HOE:Lnet/minecraft/core/component/BlockTransformer;",
				opcode = Opcodes.PUTSTATIC
			)
		)
	)
	private static BlockTransformer frozenLib$modifyHoeBlockTransformers(BlockTransformer original) {
		final BlockTransformerMappingsApi.Context context = BlockTransformerMappingsApi.createContext(original);
		BlockTransformerMappingsApi.MODIFY_HOE.invoker().modifyHoeBlockTransformer(context);
		return context.toBlockTransformer();
	}
}
