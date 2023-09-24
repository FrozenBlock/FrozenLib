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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Camera extends Item {

    public Camera(Properties settings) {
        super(settings);
    }

    private boolean canGo;

    @Override
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int i, boolean bl) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this) && player.getCooldowns().getCooldownPercent(this, 0) == 0.9F) {
                if (world.isClientSide && canGo) {
                    FrozenSharedConstants.LOGGER.warn("PLAYER HAS ACCESS TO DEV CAMERA AND HAS JUST USED IT");
                    Minecraft client = Minecraft.getInstance();
                    File directory = getPanoramaFolderName(new File(client.gameDirectory, "panoramas"));
                    File directory1 = new File(directory, "screenshots");
                    directory1.mkdir();
                    directory1.mkdirs();
                    client.grabPanoramixScreenshot(directory, 1024, 1024);
                    canGo = false;
                }
            }
        }
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    private static File getPanoramaFolderName(File directory) {
        String string = DATE_FORMAT.format(new Date());
        int i = 1;
        while (true) {
            File file = new File(directory, string + (i == 1 ? "" : "_" + i));
            if (!file.exists()) {
                return file;
            }
            ++i;
        }
    }

	@Override
    public InteractionResultHolder<ItemStack> use(Level world, @NotNull Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!user.getCooldowns().isOnCooldown(this)) {
            user.getCooldowns().addCooldown(this, 10);
            if (world.isClientSide) {
                canGo = true;
            }
            return InteractionResultHolder.success(itemStack);
        }
        return InteractionResultHolder.fail(itemStack);
    }

}
