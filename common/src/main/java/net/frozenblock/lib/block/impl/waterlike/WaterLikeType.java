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

package net.frozenblock.lib.block.impl.waterlike;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.registry.FrozenLibRegistries;import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.registries.codec.RegistryFixedCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record WaterLikeType(
	HolderSet<Block> blocks,
	Holder<SoundEvent> genericSwimSound,
	Holder<SoundEvent> hostileSwimSound,
	Holder<SoundEvent> playerSwimSound,
	Holder<SoundEvent> genericSplashSound,
	Holder<SoundEvent> hostileSplashSound,
	Holder<SoundEvent> playerSplashSound,
	Holder<SoundEvent> playerSplashHighSpeedSound,
	Holder<SoundEvent> enterSound,
	Holder<SoundEvent> exitSound,
	Holder<SoundEvent> ambientSound
) {
	public static final Codec<WaterLikeType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("blocks").forGetter(WaterLikeType::blocks),
		SoundEvent.CODEC.fieldOf("generic_swim_sound").forGetter(WaterLikeType::genericSwimSound),
		SoundEvent.CODEC.fieldOf("hostile_swim_sound").forGetter(WaterLikeType::hostileSwimSound),
		SoundEvent.CODEC.fieldOf("player_swim_sound").forGetter(WaterLikeType::playerSwimSound),
		SoundEvent.CODEC.fieldOf("generic_splash_sound").forGetter(WaterLikeType::genericSplashSound),
		SoundEvent.CODEC.fieldOf("hostile_splash_sound").forGetter(WaterLikeType::hostileSplashSound),
		SoundEvent.CODEC.fieldOf("player_splash_sound").forGetter(WaterLikeType::playerSplashSound),
		SoundEvent.CODEC.fieldOf("player_splash_high_speed_sound").forGetter(WaterLikeType::playerSplashHighSpeedSound),
		SoundEvent.CODEC.fieldOf("enter_sound").forGetter(WaterLikeType::enterSound),
		SoundEvent.CODEC.fieldOf("exit_sound").forGetter(WaterLikeType::exitSound),
		SoundEvent.CODEC.fieldOf("ambient_sound").forGetter(WaterLikeType::ambientSound)
	).apply(instance, WaterLikeType::new));
	public static final Codec<Holder<WaterLikeType>> CODEC = RegistryFixedCodec.create(FrozenLibRegistries.WATER_LIKE_TYPE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WaterLikeType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FrozenLibRegistries.WATER_LIKE_TYPE);

	public boolean contains(BlockState state) {
		return state.is(this.blocks);
	}
}
