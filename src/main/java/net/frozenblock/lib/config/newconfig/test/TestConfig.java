/*
 * Copyright (C) 2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.newconfig.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.entry.type.EntryType;
import net.frozenblock.lib.config.newconfig.instance.ConfigSettings;
import net.frozenblock.lib.config.newconfig.ConfigSerializer;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import java.util.Optional;

public class TestConfig {
	public record PoopCrap(int poop, String crap) {
		public static final Codec<PoopCrap> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
				Codec.INT.fieldOf("poop").forGetter(PoopCrap::poop),
				Codec.STRING.fieldOf("crap").forGetter(PoopCrap::crap)
			).apply(instance, PoopCrap::new)
		);

		public static final StreamCodec<FriendlyByteBuf, PoopCrap> STREAM_CODEC = StreamCodec.of(
			(buf, poopcrap) -> {
				buf.writeInt(poopcrap.poop());
				buf.writeUtf(poopcrap.crap());
			},
			(buf) -> new PoopCrap(buf.readInt(), buf.readUtf())
		);
	}
	public static final EntryType<PoopCrap> POOPCRAP_TYPE = EntryType.create(PoopCrap.CODEC, PoopCrap.STREAM_CODEC);

	public static final ConfigEntry<Boolean> TEST = new ConfigEntry<>(id("test"), EntryType.BOOL, true);
	public static final ConfigEntry<Identifier> TEST_ID = new ConfigEntry<>(id("test_id"), EntryType.IDENTIFIER, FrozenLibConstants.id("test_id"));

	public static final ConfigEntry<Boolean> TEST1_EMBEDDED = new ConfigEntry<>(id("embedded/test1"), EntryType.BOOL, true, Optional.of("whoa awesome"));
	public static final ConfigEntry<Integer> TEST2_EMBEDDED = new ConfigEntry<>(id("embedded/test2"), EntryType.INT, 67);
	public static final ConfigEntry<Integer> TEST2_EMBEDDEDX2 = new ConfigEntry<>(id("embedded/embedded/test2"), EntryType.INT, 67);
	public static final ConfigEntry<String> EXTREME_EMBEDDING = new ConfigEntry<>(id("embedded/embedded/diarrhea/diarrhea/extreme_shit"), EntryType.STRING, "gtfsvew6e7atysdfwy ^FYDTGSG EXTREME LIQUID FIRE ");

	public static final ConfigEntry<PoopCrap> POOPCRAP = new ConfigEntry<>(id("poopcrap"), POOPCRAP_TYPE, new PoopCrap(67, "41"));

	public static void init() {

	}

	private static Identifier id(String path) {
		return FrozenLibConstants.id("test_config/" + path);
	}

	static {
		Registry.register(FrozenLibRegistries.CONFIG_SETTINGS, FrozenLibConstants.id("test_config"), ConfigSettings.JSON5);
		try {
			ConfigSerializer.loadConfigs();

			ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
				try {
					ConfigSerializer.saveConfigs(true);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
