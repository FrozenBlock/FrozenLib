package net.frozenblock.lib.config.newconfig.gui;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.ChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class ConfigEntriesList extends ContainerObjectSelectionList<ConfigEntriesList.Entry> {
	private static final int ITEM_HEIGHT = 20;
	private final TestConfigScreen configScreen;
	private int maxNameWidth;

	public ConfigEntriesList(final TestConfigScreen configScreen, final Minecraft minecraft) {
		super(minecraft, configScreen.width, configScreen.layout.getContentHeight(), configScreen.layout.getHeaderHeight(), ITEM_HEIGHT);
		this.configScreen = configScreen;
		KeyMapping[] keyMappings = ArrayUtils.clone(minecraft.options.keyMappings);
		Arrays.sort(keyMappings);
		KeyMapping.Category previousCategory = null;

		for (KeyMapping key : keyMappings) {
			KeyMapping.Category category = key.getCategory();
			if (category != previousCategory) {
				previousCategory = category;
				this.addEntry(new CategoryEntry(category));
			}

			Component name = Component.translatable(key.getName());
			int width = minecraft.font.width(name);
			if (width > this.maxNameWidth) {
				this.maxNameWidth = width;
			}

			this.addEntry(new ConfigEntry(key, name));
		}
	}

	public void resetMappingAndUpdateButtons() {
		KeyMapping.resetMapping();
		this.refreshEntries();
	}

	public void refreshEntries() {
		this.children().forEach(Entry::refreshEntry);
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
		abstract void refreshEntry();
	}

	public class CategoryEntry extends Entry {
		private final FocusableTextWidget categoryName;

		public CategoryEntry(final Identifier category) {
			Objects.requireNonNull(ConfigEntriesList.this);
			super();
			this.categoryName = FocusableTextWidget.builder(Component.translatable(category.toLanguageKey("config.category")), ConfigEntriesList.this.minecraft.font)
				.alwaysShowBorder(false)
				.backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS)
				.build();
		}

		@Override
		public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
			this.categoryName.setPosition(ConfigEntriesList.this.width / 2 - this.categoryName.getWidth() / 2, this.getContentBottom() - this.categoryName.getHeight());
			this.categoryName.render(graphics, mouseX, mouseY, a);
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return List.of(this.categoryName);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return List.of(this.categoryName);
		}

		@Override
		protected void refreshEntry() {
		}
	}

	public class ConfigEntry extends Entry {
		private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
		private static final int PADDING = 10;
		private final KeyMapping key;
		private final Component name;
		private final Button changeButton;
		private final Button resetButton;
		private boolean hasCollision;

		private ConfigEntry(final KeyMapping key, final Component name) {
			Objects.requireNonNull(ConfigEntriesList.this);
			super();
			this.hasCollision = false;
			this.key = key;
			this.name = name;
			this.changeButton = Button.builder(name, button -> {
					ConfigEntriesList.this.configScreen.selectedKey = key;
					ConfigEntriesList.this.resetMappingAndUpdateButtons();
				})
				.bounds(0, 0, 75, ITEM_HEIGHT)
				.createNarration(
					defaultNarrationSupplier -> key.isUnbound()
						? Component.translatable("narrator.controls.unbound", name)
						: Component.translatable("narrator.controls.bound", name, defaultNarrationSupplier.get())
				)
				.build();
			this.resetButton = Button.builder(RESET_BUTTON_TITLE, button -> {
				key.setKey(key.getDefaultKey());
				ConfigEntriesList.this.resetMappingAndUpdateButtons();
			}).bounds(0, 0, 50, ITEM_HEIGHT).createNarration(defaultNarrationSupplier -> Component.translatable("narrator.controls.reset", name)).build();
			this.refreshEntry();
		}

		@Override
		public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
			int resetButtonX = ConfigEntriesList.this.scrollBarX() - this.resetButton.getWidth() - PADDING;
			int buttonY = this.getContentY() - 2;
			this.resetButton.setPosition(resetButtonX, buttonY);
			this.resetButton.render(graphics, mouseX, mouseY, a);
			int changeButtonX = resetButtonX - 5 - this.changeButton.getWidth();
			this.changeButton.setPosition(changeButtonX, buttonY);
			this.changeButton.render(graphics, mouseX, mouseY, a);
			graphics.drawString(ConfigEntriesList.this.minecraft.font, this.name, this.getContentX(), this.getContentYMiddle() - 9 / 2, -1);
			if (this.hasCollision) {
				int stripeWidth = 3;
				int stripeLeft = this.changeButton.getX() - 6;
				graphics.fill(stripeLeft, this.getContentY() - 1, stripeLeft + stripeWidth, this.getContentBottom(), -256);
			}
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return ImmutableList.of(this.changeButton, this.resetButton);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return ImmutableList.of(this.changeButton, this.resetButton);
		}

		@Override
		protected void refreshEntry() {
			this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
			this.resetButton.active = !this.key.isDefault();
			this.hasCollision = false;
			MutableComponent tooltip = Component.empty();
			if (!this.key.isUnbound()) {
				for (KeyMapping otherKey : ConfigEntriesList.this.minecraft.options.keyMappings) {
					if (otherKey != this.key && this.key.same(otherKey) && (!otherKey.isDefault() || !this.key.isDefault())) {
						if (this.hasCollision) {
							tooltip.append(", ");
						}

						this.hasCollision = true;
						tooltip.append(Component.translatable(otherKey.getName()));
					}
				}
			}

			if (this.hasCollision) {
				this.changeButton
					.setMessage(
						Component.literal("[ ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.YELLOW)
					);
				this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", tooltip)));
			} else {
				this.changeButton.setTooltip(null);
			}

			if (ConfigEntriesList.this.configScreen.selectedKey == this.key) {
				this.changeButton
					.setMessage(
						Component.literal("> ")
							.append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
							.append(" <")
							.withStyle(ChatFormatting.YELLOW)
					);
			}
		}
	}

}
