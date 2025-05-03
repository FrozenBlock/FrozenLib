/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.item.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class SaveableItemCooldowns {

	@NotNull
	public static List<SaveableCooldownInstance> makeSaveableCooldownInstanceList(@NotNull ServerPlayer player) {
		ArrayList<SaveableCooldownInstance> saveableCooldownInstances = new ArrayList<>();
		int tickCount = player.getCooldowns().tickCount;
		player.getCooldowns().cooldowns.forEach(
			(cooldownGroup, cooldownInstance) -> {
				Optional<Item> optionalItem = BuiltInRegistries.ITEM.getOptional(cooldownGroup);
				boolean alwaysSave = optionalItem.isPresent() && optionalItem.get().builtInRegistryHolder().is(FrozenItemTags.ALWAYS_SAVE_COOLDOWNS);
				if (alwaysSave || FrozenLibConfig.get().saveItemCooldowns) {
					saveableCooldownInstances.add(SaveableCooldownInstance.makeFromCooldownInstance(cooldownGroup, cooldownInstance, tickCount));
				}
			}
		);
		return saveableCooldownInstances;
	}

	public static void saveCooldowns(@NotNull ValueOutput output, @NotNull ServerPlayer player) {
		ValueOutput.TypedOutputList<SaveableCooldownInstance> list = output.list("FrozenLibSavedItemCooldowns", SaveableCooldownInstance.CODEC);
		for (SaveableCooldownInstance cooldown : makeSaveableCooldownInstanceList(player)) {
			list.add(cooldown);
		}
	}

	@NotNull
	public static List<SaveableCooldownInstance> readCooldowns(@NotNull ValueInput input) {
		ArrayList<SaveableCooldownInstance> saveableCooldownInstances = new ArrayList<>();
		ValueInput.TypedInputList<SaveableCooldownInstance> list = input.listOrEmpty("FrozenLibSavedItemCooldowns", SaveableCooldownInstance.CODEC);
		for (SaveableCooldownInstance cooldown : list) {
			saveableCooldownInstances.add(cooldown);
		}
		return saveableCooldownInstances;
	}

	public static void setCooldowns(@NotNull List<SaveableCooldownInstance> saveableCooldownInstances, @NotNull ServerPlayer player) {
		if (player.level().isClientSide) return;

		ItemCooldowns itemCooldowns = player.getCooldowns();
		int tickCount = itemCooldowns.tickCount;

		ServerPlayNetworking.send(player, new CooldownTickCountPacket(tickCount));

		for (SaveableCooldownInstance saveableCooldownInstance : saveableCooldownInstances) {
			int cooldownLeft = saveableCooldownInstance.cooldownLeft();
			int startTime = tickCount - (saveableCooldownInstance.totalCooldownTime() - cooldownLeft);
			int endTime = tickCount + cooldownLeft;
			ResourceLocation cooldownGroup = saveableCooldownInstance.cooldownGroup();
			itemCooldowns.cooldowns.put(cooldownGroup, new ItemCooldowns.CooldownInstance(startTime, endTime));
			ServerPlayNetworking.send(player, new ForcedCooldownPacket(cooldownGroup, startTime, endTime));
		}
	}

	public record SaveableCooldownInstance(ResourceLocation cooldownGroup, int cooldownLeft, int totalCooldownTime) {
		public static final Codec<SaveableCooldownInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("CooldownGroup").forGetter(SaveableCooldownInstance::cooldownGroup),
			Codec.INT.fieldOf("CooldownLeft").orElse(0).forGetter(SaveableCooldownInstance::cooldownLeft),
			Codec.INT.fieldOf("TotalCooldownTime").orElse(0).forGetter(SaveableCooldownInstance::totalCooldownTime)
		).apply(instance, SaveableCooldownInstance::new));


		@NotNull
		public static SaveableCooldownInstance makeFromCooldownInstance(
			@NotNull ResourceLocation cooldownGroup, @NotNull ItemCooldowns.CooldownInstance cooldownInstance, int tickCount
		) {
			int cooldownLeft = cooldownInstance.endTime - tickCount;
			int totalCooldownTime = cooldownInstance.endTime - cooldownInstance.startTime;
			return new SaveableCooldownInstance(cooldownGroup, cooldownLeft, totalCooldownTime);
		}
	}
}
