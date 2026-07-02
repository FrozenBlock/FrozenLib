package net.frozenblock.lib.platform.api.client.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface HudElementRenderer {
	void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
}
