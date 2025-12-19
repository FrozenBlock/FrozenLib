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

package net.frozenblock.lib.config.clothconfig.mixin.client;

/*import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.clothconfig.impl.DisableableWidgetInterface;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(TooltipListEntry.class)
public class TooltipListEntryMixin {

	@ModifyReturnValue(method = "getTooltip()Ljava/util/Optional;", at = @At("RETURN"), remap = false)
	public Optional<Component[]> frozenLib$getTooltip(Optional<Component[]> list) {
		final DisableableWidgetInterface disableableWidgetInterface = (DisableableWidgetInterface) this;

		if (!disableableWidgetInterface.frozenLib$getEntryPermissionType().canModify) {
			final Optional<Component> optionalComponent = FrozenClientNetworking.connectedToLan()
				? disableableWidgetInterface.frozenLib$getEntryPermissionType().lanTooltip
				: disableableWidgetInterface.frozenLib$getEntryPermissionType().tooltip;

			return optionalComponent.isPresent()
				? Optional.of(optionalComponent.orElseThrow().toFlatList().toArray(new Component[0]))
				: Optional.of(ConfigModification.EntryPermissionType.LOCKED_FOR_UNKNOWN_REASON.tooltip.orElseThrow().toFlatList().toArray(new Component[0]));
		} else if (
			disableableWidgetInterface.frozenLib$hasValidData()
			&& disableableWidgetInterface.frozenLib$isSyncable()
			&& FrozenNetworking.isMultiplayer()
			&& ConfigSyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false)
		) {
			final Component entrySyncNotice = Component.translatable("tooltip.frozenlib.entry_sync_notice");
			return list.isEmpty()
				? Optional.of(entrySyncNotice.toFlatList().toArray(new Component[0]))
				: Optional.of(Stream.concat(Arrays.stream(list.get()), Stream.of(entrySyncNotice)).toArray(Component[]::new));
		}
		return list;
	}

}
*/
