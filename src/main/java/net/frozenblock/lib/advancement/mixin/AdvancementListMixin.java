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
import net.frozenblock.lib.advancement.impl.AdvancementListInteraction;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Iterator;
import java.util.Map;

@Mixin(AdvancementList.class)
public class AdvancementListMixin implements AdvancementListInteraction {

	@Unique
	private boolean isClient = false;

	@Inject(method = "add", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void modifyAdvancements(Map<ResourceLocation, Advancement.Builder> advancements, CallbackInfo ci, Map map, boolean bl, Iterator iterator, Map.Entry entry, ResourceLocation resourceLocation, Advancement.Builder builder, Advancement advancement) {
		if (!isClient)
			AdvancementEvents.INIT.invoker().onInit(new AdvancementEvents.AdvancementHolder(resourceLocation, advancement));
	}

	@Unique
	@Override
	public void frozenLib$setClient() {
		isClient = true;
	}
}
