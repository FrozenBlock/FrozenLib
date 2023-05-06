/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.item.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.frozenlib_config.getter.FrozenLibConfigValues;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class SaveableItemCooldowns {

	public static ArrayList<SaveableCooldownInstance> makeSaveableCooldownInstanceList(@NotNull ServerPlayer player) {
		ArrayList<SaveableCooldownInstance> saveableCooldownInstances = new ArrayList<>();
		int tickCount = player.getCooldowns().tickCount;
		player.getCooldowns().cooldowns.forEach(
				((item, cooldownInstance) -> {
					if (item.builtInRegistryHolder().is(FrozenItemTags.ALWAYS_SAVE_COOLDOWNS) || FrozenLibConfigValues.CONFIG.getter().saveItemCooldowns()) {
						saveableCooldownInstances.add(SaveableCooldownInstance.makeFromCooldownInstance(item, cooldownInstance, tickCount));
					}
				})
		);
		return saveableCooldownInstances;
	}

	public static void saveCooldowns(@NotNull CompoundTag tag,  @NotNull ServerPlayer player) {
		Logger logger = FrozenMain.LOGGER;
		SaveableCooldownInstance.CODEC.listOf()
				.encodeStart(NbtOps.INSTANCE, makeSaveableCooldownInstanceList(player))
				.resultOrPartial(logger::error)
				.ifPresent((savedItemCooldownsNbt) -> tag.put("FrozenLibSavedItemCooldowns", savedItemCooldownsNbt));
	}

	public static ArrayList<SaveableCooldownInstance> readCooldowns(@NotNull CompoundTag tag) {
		ArrayList<SaveableCooldownInstance> saveableCooldownInstances = new ArrayList<>();
		if (tag.contains("FrozenLibSavedItemCooldowns", 9)) {
			Logger logger = FrozenMain.LOGGER;
			SaveableCooldownInstance.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, tag.getList("FrozenLibSavedItemCooldowns", 10)))
					.resultOrPartial(logger::error)
					.ifPresent(saveableCooldownInstances::addAll);
		}
		return saveableCooldownInstances;
	}

	public static void setCooldowns(@NotNull ArrayList<SaveableCooldownInstance> saveableCooldownInstances, @NotNull ServerPlayer player) {
		if (!player.level().isClientSide) {
			ItemCooldowns itemCooldowns = player.getCooldowns();
			int tickCount = itemCooldowns.tickCount;

			FriendlyByteBuf tickCountByteBuf = new FriendlyByteBuf(Unpooled.buffer());
			tickCountByteBuf.writeInt(tickCount);
			ServerPlayNetworking.send(player, FrozenMain.COOLDOWN_TICK_COUNT_PACKET, tickCountByteBuf);

			for (SaveableCooldownInstance saveableCooldownInstance : saveableCooldownInstances) {
				int cooldownLeft = saveableCooldownInstance.getCooldownLeft();
				int startTime = tickCount - (saveableCooldownInstance.getTotalCooldownTime() - cooldownLeft);
				int endTime = tickCount + cooldownLeft;
				Optional<Item> optionalItem = BuiltInRegistries.ITEM.getOptional(saveableCooldownInstance.getItemResourceLocation());
				if (optionalItem.isPresent()) {
					Item item = optionalItem.get();
					itemCooldowns.cooldowns.put(item, new ItemCooldowns.CooldownInstance(startTime, endTime));
					FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
					byteBuf.writeId(BuiltInRegistries.ITEM, item);
					byteBuf.writeVarInt(startTime);
					byteBuf.writeVarInt(endTime);
					ServerPlayNetworking.send(player, FrozenMain.FORCED_COOLDOWN_PACKET, byteBuf);
				}
			}
		}
	}

	public static class SaveableCooldownInstance {

		private final ResourceLocation itemResourceLocation;
		private final int cooldownLeft;
		private final int totalCooldownTime;

		public static final Codec<SaveableCooldownInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				ResourceLocation.CODEC.fieldOf("ItemResourceLocation").forGetter(SaveableCooldownInstance::getItemResourceLocation),
				Codec.INT.fieldOf("CooldownLeft").orElse(0).forGetter(SaveableCooldownInstance::getCooldownLeft),
				Codec.INT.fieldOf("TotalCooldownTime").orElse(0).forGetter(SaveableCooldownInstance::getTotalCooldownTime)
		).apply(instance, SaveableCooldownInstance::new));


		public SaveableCooldownInstance(ResourceLocation itemResourceLocation, int cooldownLeft, int totalCooldownTime) {
			this.itemResourceLocation = itemResourceLocation;
			this.cooldownLeft = cooldownLeft;
			this.totalCooldownTime = totalCooldownTime;
		}

		public static SaveableCooldownInstance makeFromCooldownInstance(@NotNull Item item, @NotNull ItemCooldowns.CooldownInstance cooldownInstance, int tickCount) {
			ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
			int cooldownLeft = cooldownInstance.endTime - tickCount;
			int totalCooldownTime = cooldownInstance.endTime - cooldownInstance.startTime;
			return new SaveableCooldownInstance(resourceLocation, cooldownLeft, totalCooldownTime);
		}

		public ResourceLocation getItemResourceLocation() {
			return this.itemResourceLocation;
		}

		public int getCooldownLeft() {
			return this.cooldownLeft;
		}

		public int getTotalCooldownTime() {
			return this.totalCooldownTime;
		}

	}

}
