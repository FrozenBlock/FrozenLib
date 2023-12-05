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

import java.lang.reflect.Field;
import me.shedaniel.clothconfig2.api.DisableableWidget;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.config.api.annotation.FieldIdentifier;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.network.ConfigSyncModification;
import net.frozenblock.lib.config.clothconfig.impl.DisableableWidgetInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(DynamicEntryListWidget.Entry.class)
public abstract class DynamicEntryListWidgetEntryMixin implements DisableableWidget, DisableableWidgetInterface {

	@Unique
	private ConfigModification.EntryPermissionType frozenLib$entryPermissionType = ConfigModification.EntryPermissionType.CAN_MODIFY;

	@Override
    public void frozenLib$addSyncData(Class<?> clazz, String identifier, Config<?> configInstance) {
		Field field = null;
		for (Field fieldToCheck : clazz.getDeclaredFields()) {
			if (
				fieldToCheck.isAnnotationPresent(FieldIdentifier.class)
					&& fieldToCheck.getAnnotation(FieldIdentifier.class).identifier().equals(identifier)
			) {
				if (field != null) FrozenLogUtils.logError("Multiple fields in " + clazz.getName() + " contain identifier " + identifier + "!", true, null);
				field = fieldToCheck;
			}
		}
		final Field finalField = field;
		FrozenLogUtils.logError("No such field with identifier " + identifier + " exists in " + clazz.getName() + "!", finalField == null, null);
		Requirement nonSyncRequirement = () -> {
			this.frozenLib$entryPermissionType = ConfigSyncModification.canModifyField(finalField, configInstance);
			return this.frozenLib$entryPermissionType.canModify;
		};
		if (this.getRequirement() != null) {
			this.setRequirement(Requirement.all(this.getRequirement(), nonSyncRequirement));
		} else {
			this.setRequirement(nonSyncRequirement);
		}
	}

	@Override
    public ConfigModification.EntryPermissionType frozenLib$getEntryPermissionType() {
		return this.frozenLib$entryPermissionType;
	}
}
