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

package net.frozenblock.lib.item.api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

public record DamageOnConsume(float amount, Optional<Holder<SoundEvent>> sound, Holder<DamageType> type) {
	public static final Codec<DamageOnConsume> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.POSITIVE_FLOAT.fieldOf("amount").forGetter(DamageOnConsume::amount),
		SoundEvent.CODEC.optionalFieldOf("sound").forGetter(DamageOnConsume::sound),
		DamageType.CODEC.fieldOf("type").forGetter(DamageOnConsume::type)
	).apply(instance, DamageOnConsume::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DamageOnConsume> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, DamageOnConsume::amount,
		SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), DamageOnConsume::sound,
		DamageType.STREAM_CODEC, DamageOnConsume::type,
		DamageOnConsume::new
	);

	public void onConsume(ItemStack itemStack, Level level, LivingEntity user) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (!user.hurtServer(serverLevel, new DamageSource(this.type), this.amount)) return;
		this.sound
			.filter(holder -> !user.isSilent())
			.ifPresent(sound -> {
				final Consumable consumable = itemStack.get(DataComponents.CONSUMABLE);
				final boolean isDrink = consumable != null && consumable.animation() == ItemUseAnimation.DRINK;
				final RandomSource random = user.getRandom();

				serverLevel.playSound(
					null,
					user.getX(), user.getY(), user.getZ(),
					sound.value(),
					user.getSoundSource(),
					isDrink ? 0.5F : random.nextBoolean() ? 0.5F : 1.0F,
					isDrink ? Mth.randomBetween(random, 0.9F, 1F) : random.triangle(1F, 0.2F)
				);
			});
	}
}
