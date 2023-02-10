package net.frozenblock.lib.testmod.config;

import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.ClientConfig;
import net.frozenblock.lib.config.api.entry.ConfigEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.gson.GsonConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.nio.file.Path;

public class TestConfig {
	public static final Config<TestConfig> INSTANCE = new GsonConfig<>(TestConfig.class, Path.of("./config/frozenlib_testmod.json"), new GsonBuilder());

	@ConfigEntry
	public boolean testToggle = true;

	public boolean notAnEntry = false;

	@Environment(EnvType.CLIENT)
	public Screen makeGui(Screen parent) {
		return ClientConfig.makeBuilder()
				.title(Component.literal("FrozenLib Testmod"))
				.save(INSTANCE::save)
				.build()
				.makeScreen(parent);
	}
}
