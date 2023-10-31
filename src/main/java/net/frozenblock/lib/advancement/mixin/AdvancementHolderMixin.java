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

package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.api.AdvancementEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementHolder.class)
public class AdvancementHolderMixin {

	@Inject(method = "read", at = @At("RETURN"))
	private static void modifyAdvancement(FriendlyByteBuf buf, CallbackInfoReturnable<AdvancementHolder> cir) {
		AdvancementEvents.INIT.invoker().onInit(cir.getReturnValue());
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(ResourceLocation resourceLocation, Advancement advancement, CallbackInfo ci) {
		//AdvancementEvents.INIT.invoker().onInit((AdvancementHolder) (Object) this);
	}
}
