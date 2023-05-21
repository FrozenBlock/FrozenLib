/*
 * Copyright 2023 FrozenBlock
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
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public class LootTableWhacker extends Item {

    public LootTableWhacker(Properties settings) {
        super(settings);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (stack.hasCustomHoverName()) {
            if (stack.getHoverName().getString().contains(":")) {
                String id = stack.getHoverName().getString();
                List<String> strings = Arrays.stream(id.split(":")).toList();
                ResourceLocation location = new ResourceLocation(strings.get(0), strings.get(1));
				BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof RandomizableContainerBlockEntity loot) {
                    loot.lootTable = location;
                    FrozenMain.log(location.toString(), true);
                } else if (blockEntity instanceof BrushableBlockEntity loot) {
					loot.lootTable = location;
					FrozenMain.log(location.toString(), true);
				}
            }
        }
        return InteractionResult.SUCCESS;
    }

}
