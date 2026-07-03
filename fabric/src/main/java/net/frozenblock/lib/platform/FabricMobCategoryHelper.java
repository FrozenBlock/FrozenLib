package net.frozenblock.lib.platform;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entity.api.category.entrypoint.FrozenMobCategoryEntrypoint;
import net.frozenblock.lib.entity.impl.category.FrozenMobCategory;
import net.frozenblock.lib.platform.service.MobCategoryHelper;

public class FabricMobCategoryHelper implements MobCategoryHelper {

	@Override
	public List<FrozenMobCategory> gatherMobCategories() {
		final ArrayList<FrozenMobCategory> newCategories = new ArrayList<>();
		FabricLoader.getInstance()
			.getEntrypointContainers("frozenlib:mob_categories", FrozenMobCategoryEntrypoint.class)
			.forEach(entrypoint -> {
				try {
					entrypoint.getEntrypoint().newCategories(newCategories);
				} catch (Throwable ignored) {
				}
			});
		return newCategories;
	}
}
