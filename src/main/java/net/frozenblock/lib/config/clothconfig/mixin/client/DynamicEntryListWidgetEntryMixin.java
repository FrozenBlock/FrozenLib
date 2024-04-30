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

import java.lang.reflect.Field;
import me.shedaniel.clothconfig2.api.DisableableWidget;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import net.frozenblock.lib.config.clothconfig.impl.DisableableWidgetInterface;
import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(DynamicEntryListWidget.Entry.class)
public abstract class DynamicEntryListWidgetEntryMixin implements DisableableWidget, DisableableWidgetInterface {

	@Unique
	private ConfigModification.EntryPermissionType frozenLib$entryPermissionType = ConfigModification.EntryPermissionType.CAN_MODIFY;

	@Unique
	private boolean frozenLib$isSyncable = true;

	@Unique
	private boolean frozenLib$hasValidData = false;

	@Unique
	@Override
    public void frozenLib$addSyncData(@NotNull Class<?> clazz, @NotNull String identifier, Config<?> configInstance) {
		if (identifier.equals("")) {
			new Exception("Cannot process sync value with empty identifier!").printStackTrace();
			return;
		}
		Field field = null;
		for (Field fieldToCheck : clazz.getDeclaredFields()) {
			EntrySyncData entrySyncData = fieldToCheck.getAnnotation(EntrySyncData.class);
			if (entrySyncData != null && !entrySyncData.value().equals("") && entrySyncData.value().equals(identifier)) {
				if (field != null) FrozenLogUtils.logError("Multiple fields in " + clazz.getName() + " contain identifier " + identifier + "!", true, null);
				field = fieldToCheck;
			}
		}
		final Field finalField = field;
		if (finalField == null) {
			new Exception("No such field with identifier " + identifier + " exists in " + clazz.getName() + "!").printStackTrace();
			return;
		}
		Requirement nonSyncRequirement = () -> {
			this.frozenLib$entryPermissionType = ConfigSyncModification.canModifyField(finalField, configInstance);
			this.frozenLib$isSyncable = ConfigSyncModification.isSyncable(finalField);
			return this.frozenLib$entryPermissionType.canModify;
		};
		if (this.getRequirement() != null) {
			this.setRequirement(Requirement.all(this.getRequirement(), nonSyncRequirement));

		} else {
			this.setRequirement(nonSyncRequirement);
		}
		this.frozenLib$hasValidData = true;
	}

	@Unique
	@Override
	public boolean frozenLib$isSyncable() {
		return this.frozenLib$isSyncable;
	}

	@Unique
	@Override
	public boolean frozenLib$hasValidData() {
		return this.frozenLib$hasValidData;
	}

	@Unique
	@Override
    public ConfigModification.EntryPermissionType frozenLib$getEntryPermissionType() {
		return this.frozenLib$entryPermissionType;
	}
}
