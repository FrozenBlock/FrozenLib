/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.clothconfig.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
		DisableableWidgetInterface disableableWidgetInterface = (DisableableWidgetInterface) this;
		if (!disableableWidgetInterface.frozenLib$getEntryPermissionType().canModify) {
			Optional<Component> optionalComponent = FrozenClientNetworking.connectedToLan() ?
					disableableWidgetInterface.frozenLib$getEntryPermissionType().lanTooltip
					: disableableWidgetInterface.frozenLib$getEntryPermissionType().tooltip;

			return optionalComponent.isPresent() ?
					Optional.of(optionalComponent.orElseThrow().toFlatList().toArray(new Component[0]))
					:
					Optional.of(ConfigModification.EntryPermissionType.LOCKED_FOR_UNKNOWN_REASON.tooltip.orElseThrow().toFlatList().toArray(new Component[0]));
		} else if (
				disableableWidgetInterface.frozenLib$hasValidData()
				&& disableableWidgetInterface.frozenLib$isSyncable()
				&& FrozenNetworking.isMultiplayer()
				&& ConfigSyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false)
		) {
			Component entrySyncNotice = Component.translatable("tooltip.frozenlib.entry_sync_notice");
			return list.isEmpty() ?
					Optional.of(entrySyncNotice.toFlatList().toArray(new Component[0]))
					:
					Optional.of(Stream.concat(Arrays.stream(list.get()), Stream.of(entrySyncNotice)).toArray(Component[]::new));
		}
		return list;
	}

}
