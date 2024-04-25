/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.ingamedevtools.item;

import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class LootTableWhacker extends Item {

    public LootTableWhacker(Properties settings) {
        super(settings);
    }

	private static final MutableComponent FAIL_NO_NAME = Component.translatable("frozenlib.loot_table_whacker.fail.no_name");

	@Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
        if (stack.hasCustomHoverName()) {
			String id = stack.getHoverName().getString();
			ResourceLocation location = new ResourceLocation(id);
			if (!level.isClientSide) {
				if (level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity loot) {
					loot.lootTable = location;
					player.displayClientMessage(Component.translatable("frozenlib.loot_table_whacker.success", location.toString()), true);
					FrozenLogUtils.log(location.toString(), true);
				} else if (level.getBlockEntity(blockPos) instanceof BrushableBlockEntity loot) {
					loot.lootTable = location;
					player.displayClientMessage(Component.translatable("frozenlib.loot_table_whacker.success", location.toString()), true);
					FrozenLogUtils.log(location.toString(), true);
				}
			}
			return InteractionResult.SUCCESS;
        } else {
			player.displayClientMessage(FAIL_NO_NAME, true);
		}
		return InteractionResult.FAIL;
    }

}
