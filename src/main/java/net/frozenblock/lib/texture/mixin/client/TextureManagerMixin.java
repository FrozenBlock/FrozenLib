/*
 * Copyright (C) 2024-2025 FrozenBlock
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

<<<<<<<< HEAD:src/main/java/net/frozenblock/lib/texture/mixin/client/TextureManagerMixin.java
package net.frozenblock.lib.texture.mixin.client;

import net.frozenblock.lib.texture.client.api.ServerTexture;
========
package net.frozenblock.lib.file.mixin.transfer.client;

import net.frozenblock.lib.file.transfer.client.ServerTexture;
>>>>>>>> 1.21.2:src/main/java/net/frozenblock/lib/file/mixin/transfer/client/TextureManagerMixin.java
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

    @Inject(method = "getTexture*", at = @At("RETURN"))
    public void frozenLib$updateServerTextureReferenceTime(CallbackInfoReturnable<AbstractTexture> info) {
        if (info.getReturnValue() instanceof ServerTexture timedTexture) {
			timedTexture.updateReferenceTime();
        }
    }
}
