/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.resource_pack.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(PackSelectionModel.class)
public class PackSelectionModelMixin {

	@ModifyReturnValue(method = "getUnselected", at = @At("RETURN"))
	public Stream<PackSelectionModel.Entry> frozenLib$hideUnselectedPacks(Stream<PackSelectionModel.Entry> selected) {
		final List<PackSelectionModel.Entry> entries = new ArrayList<>(selected.toList());
		entries.removeIf(entry -> FrozenLibModResourcePackApi.isPackHiddenFromMenu(entry.getId()));
		return entries.stream();
	}

	@ModifyReturnValue(method = "getSelected", at = @At("RETURN"))
	public Stream<PackSelectionModel.Entry> frozenLib$hideSelectedPacks(Stream<PackSelectionModel.Entry> selected) {
		final List<PackSelectionModel.Entry> entries = new ArrayList<>(selected.toList());
		entries.removeIf(entry -> FrozenLibModResourcePackApi.isPackHiddenFromMenu(entry.getId()));
		return entries.stream();
	}

}
