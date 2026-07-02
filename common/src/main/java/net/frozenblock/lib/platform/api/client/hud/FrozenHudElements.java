package net.frozenblock.lib.platform.api.client.hud;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
@UtilityClass
public class FrozenHudElements {

	public static void addFirst(Identifier id, HudElementRenderer renderer) {
		FrozenLibInitPlatformUtils.HUD_ELEMENT.addFirst(id, renderer);
	}

	public static void addLast(Identifier id, HudElementRenderer renderer) {
		FrozenLibInitPlatformUtils.HUD_ELEMENT.addLast(id, renderer);
	}

	public static void attachElementBefore(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {
		FrozenLibInitPlatformUtils.HUD_ELEMENT.attachElementBefore(vanillaElementId, id, renderer);
	}

	public static void attachElementAfter(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {
		FrozenLibInitPlatformUtils.HUD_ELEMENT.attachElementAfter(vanillaElementId, id, renderer);
	}
}
