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

package net.frozenblock.lib.config.clothconfig.mixin.client;

import java.util.Optional;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.clothconfig.impl.FieldBuilderInterface;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TooltipListEntry.class)
public class TooltipListEntryMixin {

	@Inject(method = "getTooltip()Ljava/util/Optional;", at = @At("HEAD"), cancellable = true, remap = false)
	public void getTooltip(CallbackInfoReturnable<Optional<Component[]>> info) {
		FieldBuilder<?, ?, ?> fieldBuilder = FrozenClothConfig.getFieldBuilder(TooltipListEntry.class.cast(this));
		if (fieldBuilder != null) {
			FieldBuilderInterface fieldBuilderInterface = FrozenClothConfig.getFieldBuilderInterface(fieldBuilder);
			if (!fieldBuilderInterface.frozenLib$getModifyType().canModify) {
				boolean present = fieldBuilderInterface.frozenLib$getModifyType().tooltip.isPresent();
				info.setReturnValue(
					present ?
						Optional.of(fieldBuilderInterface.frozenLib$getModifyType().tooltip.get().toFlatList().toArray(new Component[0]))
						:
						Optional.empty()
				);
			}
		}
	}

}
