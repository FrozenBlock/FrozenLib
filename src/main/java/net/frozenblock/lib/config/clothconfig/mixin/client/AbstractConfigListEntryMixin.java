package net.frozenblock.lib.config.clothconfig.mixin.client;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.clothconfig.impl.AbstractConfigListEntryInterface;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractConfigListEntry.class)
public class AbstractConfigListEntryMixin implements AbstractConfigListEntryInterface {

	@Unique
	@Nullable
	private FieldBuilder frozenLib$fieldBuilder;


	@Override
	public void setFieldBuilder(FieldBuilder fieldBuilder) {
		this.frozenLib$fieldBuilder = fieldBuilder;
	}

	@Override
	@Nullable
	public FieldBuilder getFieldBuilder() {
		return this.frozenLib$fieldBuilder;
	}

}
