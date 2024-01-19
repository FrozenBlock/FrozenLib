package net.frozenblock.lib.datafix.impl;

import com.mojang.datafixers.DSL;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.datafix.api.entrypoint.FrozenDataFixTypesEntrypoint;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class InternalFrozenDataFixTypes implements FrozenDataFixTypesEntrypoint {
	private static final DSL.TypeReference SAVED_DATA_WIND = () -> "saved_data/frozenlib_wind";

	@Override
	public void newCategories(@NotNull ArrayList<FrozenDataFixType> context) {
		context.add(new FrozenDataFixType(FrozenSharedConstants.id("saved_data_wind"), SAVED_DATA_WIND));
	}
}
