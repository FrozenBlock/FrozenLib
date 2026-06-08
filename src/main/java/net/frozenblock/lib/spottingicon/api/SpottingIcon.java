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

package net.frozenblock.lib.spottingicon.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public record SpottingIcon(Identifier texture, Attributes attributes) {
	public static final Codec<SpottingIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Identifier.CODEC.fieldOf("texture").forGetter(SpottingIcon::texture),
		Attributes.CODEC.fieldOf("attributes").forGetter(SpottingIcon::attributes)
	).apply(instance, SpottingIcon::new));
	public static final StreamCodec<ByteBuf, SpottingIcon> STREAM_CODEC = StreamCodec.composite(
		Identifier.STREAM_CODEC, SpottingIcon::texture,
		Attributes.STREAM_CODEC, SpottingIcon::attributes,
		SpottingIcon::new
	);
	public static final Codec<List<SpottingIcon>> LIST_CODEC = CODEC.listOf();
	public static final StreamCodec<ByteBuf, List<SpottingIcon>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Identifier texture;
		private final List<Fade> scalers = new ArrayList<>();
		private final List<Fade> faders = new ArrayList<>();

		private Builder() {}

		public Builder texture(Identifier texture) {
			this.texture = texture;
			return this;
		}

		public Builder scaler(float startDistance, float endDistance, float startValue, float endValue) {
			this.scalers.add(new Fade(startDistance, endDistance, startValue, endValue));
			return this;
		}

		public Builder scale(float value) {
			this.scalers.add(new Fade(0F, 0F, value, value));
			return this;
		}

		public Builder fader(float startDistance, float endDistance, float startValue, float endValue) {
			this.faders.add(new Fade(startDistance, endDistance, startValue, endValue));
			return this;
		}

		public Builder transparency(float value) {
			this.faders.add(new Fade(0F, 0F, value, value));
			return this;
		}

		public SpottingIcon build() {
			if (this.texture == null) throw new IllegalStateException("Spotting Icon texture cannot be null!");
			return new SpottingIcon(this.texture, new Attributes(this.scalers, this.faders));
		}
	}

	public record Attributes(List<Fade> scalers, List<Fade> faders) {
		public static final Codec<Attributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Fade.LIST_CODEC.fieldOf("scalers").forGetter(Attributes::scalers),
			Fade.LIST_CODEC.fieldOf("faders").forGetter(Attributes::faders)
		).apply(instance, Attributes::new));
		public static final StreamCodec<ByteBuf, Attributes> STREAM_CODEC = StreamCodec.composite(
			Fade.LIST_STREAM_CODEC, Attributes::scalers,
			Fade.LIST_STREAM_CODEC, Attributes::faders,
			Attributes::new
		);

		public float calculateScale(double distance) {
			float scale = 1F;
			for (Fade fade : this.scalers) {
				scale *= fade.calculate(distance);
			}
			return Math.max(scale, 0F);
		}

		public float calculateTransparency(double distance) {
			float transparency = 1F;
			for (Fade fade : this.faders) {
				transparency *= fade.calculate(distance);
			}
			return Mth.clamp(transparency, 0F, 1F);
		}
	}

	private record Fade(float startDistance, float endDistance, float startValue, float endValue) {
		public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("start_distance").forGetter(Fade::startDistance),
			Codec.FLOAT.fieldOf("end_distance").forGetter(Fade::endDistance),
			Codec.FLOAT.fieldOf("start_value").forGetter(Fade::startValue),
			Codec.FLOAT.fieldOf("end_value").forGetter(Fade::endValue)
		).apply(instance, Fade::new));
		public static final StreamCodec<ByteBuf, Fade> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, Fade::startDistance,
			ByteBufCodecs.FLOAT, Fade::endDistance,
			ByteBufCodecs.FLOAT, Fade::startValue,
			ByteBufCodecs.FLOAT, Fade::endValue,
			Fade::new
		);
		public static final Codec<List<Fade>> LIST_CODEC = CODEC.listOf();
		public static final StreamCodec<ByteBuf, List<Fade>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

		public float calculate(double distance) {
			if (distance < this.startDistance) return this.startValue;
			if (distance > this.endDistance) return this.endValue;

			final float distanceProgress =  ((float)(distance - this.startDistance)) / (this.endDistance - this.startDistance);
			return Mth.lerp(distanceProgress, this.startValue, this.endValue);
		}
	}
}
