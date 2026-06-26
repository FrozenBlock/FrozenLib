/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.structure.api.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.frozenblock.lib.levelgen.structure.impl.FrozenLibRuleBlockEntityModifiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.jetbrains.annotations.Nullable;

public record AppendSherds(List<ItemStackTemplate> sherds, float chancePerSlot) implements RuleBlockEntityModifier {
	public static final MapCodec<AppendSherds> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStackTemplate.CODEC.listOf().fieldOf("sherds").forGetter(modifier -> modifier.sherds),
		Codec.FLOAT.fieldOf("chance_per_slot").orElse(0.75F).forGetter(modifier -> modifier.chancePerSlot)
	).apply(instance, AppendSherds::new));

	public AppendSherds(float chancePerSlot, ItemStackTemplate... sherd) {
		this(List.of(sherd), chancePerSlot);
	}

	public AppendSherds(List<ItemStackTemplate> sherds, float chancePerSlot) {
		this.sherds = sherds;
		this.chancePerSlot = chancePerSlot;
		if (this.sherds.isEmpty()) throw new IllegalArgumentException("AppendSherds requires at least one sherd!");
	}

	@Override
	public CompoundTag apply(RandomSource random, @Nullable CompoundTag existingTag) {
		final Function<Optional<ItemStackTemplate>, Optional<ItemStackTemplate>> sherdSelector = original -> {
			return random.nextFloat() <= this.chancePerSlot
				? Optional.of(this.getRandomSherd(random))
				: original;
		};

		final CompoundTag compoundTag = existingTag == null ? new CompoundTag() : existingTag.copy();
		final PotDecorations initialPotDecorations = compoundTag.read("sherds", PotDecorations.CODEC).orElse(PotDecorations.EMPTY);
		final PotDecorations potDecorations = new PotDecorations(
			sherdSelector.apply(initialPotDecorations.back()),
			sherdSelector.apply(initialPotDecorations.left()),
			sherdSelector.apply(initialPotDecorations.right()),
			sherdSelector.apply(initialPotDecorations.front())
		);

		compoundTag.store("sherds", PotDecorations.CODEC, potDecorations);
		return compoundTag;
	}

	public ItemStackTemplate getRandomSherd(RandomSource random) {
		return Util.getRandom(this.sherds, random);
	}

	@Override
	public RuleBlockEntityModifierType<?> getType() {
		return FrozenLibRuleBlockEntityModifiers.APPEND_SHERDS;
	}
}
