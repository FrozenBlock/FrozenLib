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

import java.lang.reflect.Field;
import me.shedaniel.clothconfig2.api.DisableableWidget;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibLogUtils;
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
		if (identifier.isEmpty()) {
			new Exception("Cannot process sync value with empty identifier!").printStackTrace();
			return;
		}
		Field field = null;
		for (Field fieldToCheck : clazz.getDeclaredFields()) {
			EntrySyncData entrySyncData = fieldToCheck.getAnnotation(EntrySyncData.class);
			if (entrySyncData != null && !entrySyncData.value().isEmpty() && entrySyncData.value().equals(identifier)) {
				if (field != null) FrozenLibLogUtils.logError("Multiple fields in " + clazz.getName() + " contain identifier " + identifier + "!", true, null);
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
