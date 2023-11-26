package net.frozenblock.lib.config.clothconfig.impl;

import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface AbstractConfigListEntryInterface {

	void setFieldBuilder(FieldBuilder fieldBuilder);

	@Nullable
	FieldBuilder getFieldBuilder();

}
