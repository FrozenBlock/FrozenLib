package net.frozenblock.lib.config.api.client.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OptionList extends ContainerObjectSelectionList<OptionList.Entry> {

	private final Config<?> config;

	public OptionList(Config<?> config, Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
		super(minecraft, width, height, y0, y1, itemHeight);
		this.config = config;
		this.centerListVertically = false;
	}

	public int addBig(Option<?> option) {
		return this.addEntry(Entry.big(this.config, this.width, option));
	}

	public void addSmall(Option<?> leftOption, @Nullable Option<?> rightOption) {
		this.addEntry(Entry.small(this.config, this.width, leftOption, rightOption));
	}

	public void addSmall(Option<?>[] options) {
		for(int i = 0; i < options.length; i += 2) {
			this.addSmall(options[i], i < options.length - 1 ? options[i + 1] : null);
		}
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	@Nullable
	public AbstractWidget findOption(Option<?> option) {
		for(Entry entry : this.children()) {
			AbstractWidget abstractWidget = entry.options.get(option);
			if (abstractWidget != null) {
				return abstractWidget;
			}
		}

		return null;
	}

	public Optional<AbstractWidget> getMouseOver(double mouseX, double mouseY) {
		for(Entry entry : this.children()) {
			for(AbstractWidget abstractWidget : entry.children) {
				if (abstractWidget.isMouseOver(mouseX, mouseY)) {
					return Optional.of(abstractWidget);
				}
			}
		}

		return Optional.empty();
	}

	protected static class Entry extends ContainerObjectSelectionList.Entry<OptionList.Entry> {
		final Map<Option<?>, AbstractWidget> options;
		final List<AbstractWidget> children;

		private Entry(Map<Option<?>, AbstractWidget> options) {
			this.options = options;
			this.children = ImmutableList.copyOf(options.values());
		}

		public static Entry big(Config<?> config, int guiWidth, Option<?> option) {
			return new Entry(ImmutableMap.of(option, option.createButton(config, guiWidth / 2 - 155, 0, 310)));
		}

		public static Entry small(Config<?> config, int guiWidth, Option<?> leftOption, @Nullable Option<?> rightOption) {
			AbstractWidget abstractWidget = leftOption.createButton(config, guiWidth / 2 - 155, 0, 150);
			return rightOption == null
					? new Entry(ImmutableMap.of(leftOption, abstractWidget))
					: new Entry(ImmutableMap.of(leftOption, abstractWidget, rightOption, rightOption.createButton(config, guiWidth / 2 - 155 + 160, 0, 150)));
		}

		@Override
		public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
			this.children.forEach(button -> {
				button.y = top;
				button.render(poseStack, mouseX, mouseY, partialTick);
			});
		}

		public List<Option<?>> options() {
			return this.options.keySet().stream().toList();
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return this.children;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return this.children;
		}
	}
}
