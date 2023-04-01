package net.frozenblock.lib.config.api.client.option;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AbstractOptionContainerWidget extends AbstractWidget {

	private final List<Option.SubOption<?>> children = new ArrayList<>();

	public AbstractOptionContainerWidget(int x, int y, int width, int height, Component message, List<Option.SubOption<?>> children) {
		super(x, y, width, height, message);
		this.children.addAll(children);
	}

	@SafeVarargs
	public AbstractOptionContainerWidget(int x, int y, int width, int height, Component message, Option.SubOption<?>... children) {
		this(x, y, width, height, message, Arrays.stream(children).toList());
	}

	public void addChild(Option.SubOption<?> child) {
		this.children.add(child);
	}

	public List<Option.SubOption<?>> getChildren() {
		return this.children;
	}

	@Override
	protected void renderBg(PoseStack matrices, Minecraft minecraft, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}
}
