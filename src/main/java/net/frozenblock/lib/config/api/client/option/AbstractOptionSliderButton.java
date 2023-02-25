package net.frozenblock.lib.config.api.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;

@Environment(EnvType.CLIENT)
public abstract class AbstractOptionSliderButton extends AbstractSliderButton {
	protected final Config<?> config;

	public AbstractOptionSliderButton(Config<?> config, int x, int y, int width, int height, double value) {
		super(x, y, width, height, CommonComponents.EMPTY, value);
		this.config = config;
	}
}
