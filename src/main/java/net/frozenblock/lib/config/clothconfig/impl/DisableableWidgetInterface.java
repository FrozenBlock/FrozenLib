package net.frozenblock.lib.config.clothconfig.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;

@Environment(EnvType.CLIENT)
public interface DisableableWidgetInterface {

	void frozenLib$addSyncData(Class<?> clazz, String identifier, Config<?> configInstance);


	ConfigModification.EntryPermissionType frozenLib$getEntryPermissionType();
}
