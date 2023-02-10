package net.frozenblock.lib.testmod.config;

import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.client.ClientConfig;
import net.frozenblock.lib.config.api.entry.ConfigEntry;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.gson.GsonConfig;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import java.nio.file.Path;

public class TestConfig {
	private static final Config<TestConfig> INSTANCE = ConfigRegistry.register(
			new GsonConfig<>(FrozenTestMain.MOD_ID, TestConfig.class)
	);

	private static final TypedEntry<SoundEvent> SOUND_EVENT = ConfigRegistry.register(
			new TypedEntry<>(
					FrozenTestMain.MOD_ID,
					SoundEvent.class,
					SoundEvent.CODEC
			)
	);

	@ConfigEntry
	public boolean testToggle = true;

	@ConfigEntry
	public int testInt = 69;

	@ConfigEntry
	public long testLong = 69420L;

	@ConfigEntry
	public float testFloat = 69.420F;

	@ConfigEntry
	public double testDouble = 69.4206942069420D;

	public boolean notAnEntry = false;

	public static Config<TestConfig> get() {
		return INSTANCE;
	}

	@Environment(EnvType.CLIENT)
	public Screen makeGui(Screen parent) {
		return ClientConfig.makeBuilder()
				.title(Component.literal("FrozenLib Testmod"))
				.save(INSTANCE::save)
				.build()
				.makeScreen(parent);
	}
}
