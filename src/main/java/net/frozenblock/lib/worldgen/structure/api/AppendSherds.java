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

package net.frozenblock.lib.worldgen.structure.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.FrozenRuleBlockEntityModifiers;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AppendSherds implements RuleBlockEntityModifier {
	public static final MapCodec<AppendSherds> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("sherds").forGetter(modifier -> modifier.sherds),
			Codec.FLOAT.fieldOf("chance_per_slot").orElse(0.75F).forGetter(modifier -> modifier.chancePerSlot),
			Codec.BOOL.fieldOf("default_to_brick").orElse(true).forGetter(modifier -> modifier.defaultToBrick)
		).apply(instance, AppendSherds::new)
	);
	private final List<Item> sherds;
	private final float chancePerSlot;
	private final boolean defaultToBrick;

	public AppendSherds(float chancePerSlot, boolean defaultToBrick, Item... sherd) {
		this(List.of(sherd), chancePerSlot, defaultToBrick);
	}

	public AppendSherds(List<Item> sherds, float chancePerSlot, boolean defaultToBrick) {
		this.sherds = sherds;
		this.chancePerSlot = chancePerSlot;
		this.defaultToBrick = defaultToBrick;
		if (this.sherds.isEmpty()) throw new IllegalArgumentException("AppendSherds requires at least one sherd!");
	}

	@Override
	public CompoundTag apply(@NotNull RandomSource random, @Nullable CompoundTag nbt) {
		CompoundTag compoundTag = nbt == null ? new CompoundTag() : nbt.copy();
		Item[] chosenSherds = new Item[4];
		List<Item> orderedDecorations = PotDecorations.load(nbt).ordered();
		for (int i = 0; i < chosenSherds.length; i++) {
			if (random.nextFloat() <= this.chancePerSlot) {
				chosenSherds[i] = this.getRandomSherd(random);
			} else {
				chosenSherds[i] = this.defaultToBrick ? Items.BRICK : orderedDecorations.get(i);
			}
		}
		PotDecorations processedDecorations = new PotDecorations(
			chosenSherds[0],
			chosenSherds[1],
			chosenSherds[2],
			chosenSherds[3]
		);
		return processedDecorations.save(compoundTag);
	}

	public Item getRandomSherd(@NotNull RandomSource random) {
		return Util.getRandom(this.sherds, random);
	}

	@Override
	public @NotNull RuleBlockEntityModifierType<?> getType() {
		return FrozenRuleBlockEntityModifiers.APPEND_SHERDS;
	}
}
