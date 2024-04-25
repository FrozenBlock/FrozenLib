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
