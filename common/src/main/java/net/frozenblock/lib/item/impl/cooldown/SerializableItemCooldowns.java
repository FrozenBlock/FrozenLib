/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.item.impl.cooldown;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;
import net.frozenblock.lib.tag.api.FrozenLibItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemCooldowns;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record SerializableItemCooldowns(List<ItemCooldown> cooldowns) {
	public static final Codec<SerializableItemCooldowns> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ItemCooldown.CODEC.listOf().fieldOf("serializableItemCooldowns").forGetter(SerializableItemCooldowns::cooldowns)
	).apply(instance, SerializableItemCooldowns::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SerializableItemCooldowns> STREAM_CODEC = StreamCodec.composite(
		ItemCooldown.STREAM_CODEC.apply(ByteBufCodecs.list()), SerializableItemCooldowns::cooldowns,
		SerializableItemCooldowns::new
	);
	public static final DataAttachmentType<SerializableItemCooldowns> ATTACHMENT = DataAttachmentType.create(
		FrozenLibConstants.id("item_cooldowns"),
		builder -> builder
			.persistent(CODEC)
			.copyOnDeath()
	);

	public static void init() {}

	public static SerializableItemCooldowns of(ItemCooldowns source) {
		final List<ItemCooldown> cooldowns = new ArrayList<>();
		final int tickCount = source.tickCount;
		source.cooldowns.forEach((group, cooldown) -> {
			final boolean alwaysSave = BuiltInRegistries.ITEM.getOptional(group)
				.map(item -> item.builtInRegistryHolder().is(FrozenLibItemTags.ALWAYS_SAVE_COOLDOWNS))
				.orElse(false);
			if (!alwaysSave && !FrozenLibConfig.SAVE_ITEM_COOLDOWNS.get()) return;

			cooldowns.add(ItemCooldown.of(group, cooldown, tickCount));
		});

		return new SerializableItemCooldowns(List.copyOf(cooldowns));
	}

	public void syncWithTarget(ServerPlayer target) {
		final ItemCooldowns itemCooldowns = target.getCooldowns();
		final int tickCount = itemCooldowns.tickCount;
		NetworkingHelper.sendToPlayer(target, new SerializableItemCooldownsSyncPacket(tickCount, this));

		for (ItemCooldown cooldown : this.cooldowns) {
			final Identifier group = cooldown.group;
			final int remainingTime = cooldown.remainingTime;
			final int startTime = tickCount - (cooldown.totalTime - remainingTime);
			final int endTime = tickCount + remainingTime;
			itemCooldowns.cooldowns.put(group, new ItemCooldowns.CooldownInstance(startTime, endTime));
		}
	}

	public record ItemCooldown(Identifier group, int remainingTime, int totalTime) {
		public static final Codec<ItemCooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("group").forGetter(ItemCooldown::group),
			Codec.INT.fieldOf("remaining_time").orElse(0).forGetter(ItemCooldown::remainingTime),
			Codec.INT.fieldOf("total_time").orElse(0).forGetter(ItemCooldown::totalTime)
		).apply(instance, ItemCooldown::new));
		public static final StreamCodec<FriendlyByteBuf, ItemCooldown> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, ItemCooldown::group,
			ByteBufCodecs.VAR_INT, ItemCooldown::remainingTime,
			ByteBufCodecs.VAR_INT, ItemCooldown::totalTime,
			ItemCooldown::new
		);

		public static ItemCooldown of(Identifier group, ItemCooldowns.CooldownInstance cooldown, int ticks) {
			final int cooldownLeft = cooldown.endTime() - ticks;
			final int totalCooldownTime = cooldown.endTime() - cooldown.startTime();
			return new ItemCooldown(group, cooldownLeft, totalCooldownTime);
		}
	}
}
