package net.frozenblock.lib.testmod.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.ClientConfig;
import net.frozenblock.lib.config.api.entry.Exclude;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.gson.GsonConfig;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class TestConfig {

	@Exclude
	private static final Config<TestConfig> INSTANCE = ConfigRegistry.register(
			new GsonConfig<>(FrozenTestMain.MOD_ID, TestConfig.class)
	);

	@Exclude
	private static final TypedEntryType<SoundEvent> SOUND_EVENT = ConfigRegistry.register(
			new TypedEntryType<>(
					FrozenTestMain.MOD_ID,
					SoundEvent.class,
					SoundEvent.CODEC
			)
	);

	public boolean testToggle = true;

	public int testInt = 69;

	public long testLong = 69420L;

	public float testFloat = 69.420F;

	public double testDouble = 69.4206942069420D;

	public TypedEntry<SoundEvent> randomSound = new TypedEntry<>(
			SOUND_EVENT, SoundEvents.BEE_LOOP
	);

	public TypedEntry<Integer> typedInt = new TypedEntry<>(
			TypedEntryType.INTEGER, 69
	);

	@Exclude
	public boolean notABoolean = false;

	@Exclude
	public int notAnInt = 0;

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
