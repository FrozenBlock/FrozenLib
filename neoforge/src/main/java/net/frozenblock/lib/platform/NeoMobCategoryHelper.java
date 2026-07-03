package net.frozenblock.lib.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import net.frozenblock.lib.entity.api.category.entrypoint.FrozenMobCategoryEntrypoint;
import net.frozenblock.lib.entity.impl.category.FrozenMobCategory;
import net.frozenblock.lib.platform.service.MobCategoryHelper;

public class NeoMobCategoryHelper implements MobCategoryHelper {

	@Override
	public List<FrozenMobCategory> gatherMobCategories() {
		final ArrayList<FrozenMobCategory> newCategories = new ArrayList<>();
		ServiceLoader.load(FrozenMobCategoryEntrypoint.class, NeoMobCategoryHelper.class.getClassLoader())
			.forEach(entrypoint -> {
				try {
					entrypoint.newCategories(newCategories);
				} catch (Throwable ignored) {
				}
			});
		return newCategories;
	}
}
