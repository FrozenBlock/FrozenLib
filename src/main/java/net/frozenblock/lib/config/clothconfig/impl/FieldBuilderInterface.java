package net.frozenblock.lib.config.clothconfig.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.network.ConfigSyncModification;

@Environment(EnvType.CLIENT)
public interface FieldBuilderInterface {

	void addSyncData(Class<?> clazz, String identifier);

	ConfigSyncModification.ModifyType getModifyType();

}
