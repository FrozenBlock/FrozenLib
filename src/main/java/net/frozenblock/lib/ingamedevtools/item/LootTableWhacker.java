/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.ingamedevtools.item;

import java.util.Arrays;
import java.util.List;
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
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class LootTableWhacker extends Item {

    public LootTableWhacker(Properties settings) {
        super(settings);
    }

	private static final MutableComponent FAIL_NO_NAME = Component.translatable("frozenlib.loot_table_whacker.fail.no_name");
	private static final MutableComponent FAIL_NO_COLON = Component.translatable("frozenlib.loot_table_whacker.fail.no_colon");

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
            if (stack.getHoverName().getString().contains(":")) {
                String id = stack.getHoverName().getString();
                List<String> strings = Arrays.stream(id.split(":")).toList();
                ResourceLocation location = new ResourceLocation(strings.get(0), strings.get(1));
				if (!level.isClientSide) {
					if (level.getServer().getLootData().getLootTable(location) != LootTable.EMPTY) {
						if (level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity loot) {
							loot.lootTable = location;
							player.displayClientMessage(Component.translatable("frozenlib.loot_table_whacker.success", location.toString()), true);
							FrozenLogUtils.log(location.toString(), true);
						}
					} else {
						player.displayClientMessage(Component.translatable("frozenlib.loot_table_whacker.fail.no_loot_table", location.toString()), true);
					}
				}
            } else {
				player.displayClientMessage(FAIL_NO_COLON, true);
			}
        } else {
			player.displayClientMessage(FAIL_NO_NAME, true);
		}
        return InteractionResult.SUCCESS;
    }

}
