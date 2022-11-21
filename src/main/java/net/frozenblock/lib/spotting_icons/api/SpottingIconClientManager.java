package net.frozenblock.lib.spotting_icons.api;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public final class SpottingIconClientManager {
	private static final ArrayList<SpottingIconManager> ICON_MANAGERS = new ArrayList<>();

	public static void addManager(SpottingIconManager iconManager) {
		if (!ICON_MANAGERS.contains(iconManager)) {
			ICON_MANAGERS.add(iconManager);
		}
	}

	public static void removeManager(SpottingIconManager iconManager) {
		ICON_MANAGERS.remove(iconManager);
	}

	public static void onResourceReload(ResourceManager manager) {
		for (SpottingIconManager iconManager : (ArrayList<SpottingIconManager>)ICON_MANAGERS.clone()) {
			if (iconManager.icon != null) {
				iconManager.clientHasIconResource = hasTexture(manager, iconManager.icon.getTexture());
			}
		}
	}

	public static boolean hasTexture(ResourceManager manager, ResourceLocation resourceLocation) {
		return manager.getResource(resourceLocation).isPresent();
	}

	public static boolean hasTexture(ResourceLocation resourceLocation) {
		return Minecraft.getInstance().getResourceManager().getResource(resourceLocation).isPresent();
	}

}
