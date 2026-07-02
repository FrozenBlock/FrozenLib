package net.frozenblock.lib.platform.service;

import net.frozenblock.lib.platform.api.client.hud.HudElementRenderer;
import net.frozenblock.lib.platform.api.client.hud.VanillaHudAnchor;
import net.minecraft.resources.Identifier;

public interface HudElementHelper {

	void addFirst(Identifier id, HudElementRenderer renderer);

	void addLast(Identifier id, HudElementRenderer renderer);

	void attachElementBefore(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer);

	void attachElementAfter(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer);
}
