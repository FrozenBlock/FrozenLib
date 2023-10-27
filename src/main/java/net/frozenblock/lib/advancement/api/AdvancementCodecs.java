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

package net.frozenblock.lib.advancement.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Experimental
public final class AdvancementCodecs {
	private AdvancementCodecs() {}

	public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
			CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(stack -> Optional.ofNullable(stack.getTag()))
		).apply(instance, (item, nbt) -> new ItemStack(item, 1, nbt))
	);

	public static final Codec<DisplayInfo> DISPLAY_INFO_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ITEM_STACK_CODEC.fieldOf("icon").forGetter(DisplayInfo::getIcon),
			ExtraCodecs.COMPONENT.fieldOf("title").forGetter(DisplayInfo::getTitle),
			ExtraCodecs.COMPONENT.fieldOf("description").forGetter(DisplayInfo::getDescription),
			ResourceLocation.CODEC.optionalFieldOf("background").forGetter(displayInfo -> Optional.ofNullable(displayInfo.getBackground())),
			Codec.STRING.fieldOf("frame").forGetter(displayInfo -> displayInfo.getFrame().getName()),
			Codec.BOOL.fieldOf("showToast").forGetter(DisplayInfo::shouldShowToast),
			Codec.BOOL.fieldOf("announceChat").forGetter(DisplayInfo::shouldAnnounceChat),
			Codec.BOOL.fieldOf("hidden").forGetter(DisplayInfo::isHidden)
		).apply(instance, (icon, title, description, background, frame, showToast, announceChat, hidden) -> new DisplayInfo(icon, title, description, background.orElse(null), FrameType.byName(frame), showToast, announceChat, hidden))
	);

	public static final Codec<CommandFunction.CacheableFunction> CACHEABLE_FUNCTION_CODEC = ResourceLocation.CODEC.xmap(CommandFunction.CacheableFunction::new, CommandFunction.CacheableFunction::getId);

	public static final Codec<AdvancementRewards> ADVANCEMENT_REWARDS_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.fieldOf("experience").forGetter(rewards -> rewards.experience),
			ResourceLocation.CODEC.listOf().fieldOf("loot").forGetter(rewards -> Arrays.asList(rewards.loot)),
			ResourceLocation.CODEC.listOf().fieldOf("recipes").forGetter(rewards -> Arrays.asList(rewards.getRecipes())),
			CACHEABLE_FUNCTION_CODEC.fieldOf("function").forGetter(rewards -> rewards.function)
		).apply(instance, (experience, loot, recipes, function) -> new AdvancementRewards(experience, loot.toArray(new ResourceLocation[]{}), recipes.toArray(new ResourceLocation[]{}), function))
	);

	public static final Codec<AdvancementRequirements> ADVANCEMENT_REQUIREMENTS_CODEC = Codec.STRING.listOf().listOf().xmap(
		lists -> new AdvancementRequirements(lists.stream().map(list -> list.toArray(new String[]{})).toList().toArray(new String[][]{})),
		requirements -> Arrays.stream(requirements.requirements).map(array -> Arrays.stream(array).toList()).toList()
	);

	public static final Codec<Advancement> ADVANCEMENT_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent),
			DISPLAY_INFO_CODEC.optionalFieldOf("display").forGetter(Advancement::display),
			ADVANCEMENT_REWARDS_CODEC.fieldOf("rewards").forGetter(Advancement::rewards),
			Codec.compoundList(Codec.STRING, Codec.unit((Criterion<?>) null)).fieldOf("criteria").forGetter(advancement -> {
				var entrySet = advancement.criteria().entrySet().stream();
				return (List) entrySet.map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList();
			}),
			ADVANCEMENT_REQUIREMENTS_CODEC.fieldOf("requirements").forGetter(Advancement::requirements),
			Codec.BOOL.fieldOf("sendsTelemetryEvent").forGetter(Advancement::sendsTelemetryEvent),
			ExtraCodecs.COMPONENT.optionalFieldOf("name").forGetter(advancement -> advancement.name())
		).apply(instance, (parent, display, rewards, criteria, requirements, sendsTelemetryEvent, name) -> new Advancement(
			parent,
			display,
			rewards,
			Map.ofEntries(criteria.stream().map(pair -> Map.<String, Criterion<?>>entry(pair.getFirst(), pair.getSecond())).toList().toArray(new Map.Entry[]{})),
			requirements,
			sendsTelemetryEvent,
			name
		))
	);
}
