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

package net.frozenblock.lib.screenshake.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ScreenShake(float intensity, Optional<Vec3> position, float minDistance, float maxDistance, long startTime, int duration, int falloffStartDuration) {
	public static final Codec<ScreenShake> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("intensity", 1F).forGetter(ScreenShake::intensity),
		Vec3.CODEC.optionalFieldOf("position").forGetter(ScreenShake::position),
		ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("min_distance", 0F).forGetter(ScreenShake::minDistance),
		ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("max_distance", 8F).forGetter(ScreenShake::maxDistance),
		Codec.LONG.fieldOf("start_time").forGetter(ScreenShake::startTime),
		ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", 20).forGetter(ScreenShake::duration),
		ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("falloffStartDuration", 5).forGetter(ScreenShake::falloffStartDuration)
	).apply(instance, ScreenShake::new));
	public static final StreamCodec<ByteBuf, ScreenShake> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, ScreenShake::intensity,
		ByteBufCodecs.optional(Vec3.STREAM_CODEC), ScreenShake::position,
		ByteBufCodecs.FLOAT, ScreenShake::minDistance,
		ByteBufCodecs.FLOAT, ScreenShake::maxDistance,
		ByteBufCodecs.VAR_LONG, ScreenShake::startTime,
		ByteBufCodecs.VAR_INT, ScreenShake::duration,
		ByteBufCodecs.VAR_INT, ScreenShake::falloffStartDuration,
		ScreenShake::new
	);
	public static final Codec<List<ScreenShake>> LIST_CODEC = CODEC.listOf();
	public static final StreamCodec<ByteBuf, List<ScreenShake>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

	public static Builder builder(Level level, Optional<Vec3> position) {
		return new Builder(level.getGameTime(), position);
	}

	public static Builder builder(Level level) {
		return builder(level, Optional.empty());
	}

	public static Builder builder(Entity entity) {
		return builder(entity.level());
	}

	public boolean expired(long gameTime) {
		return gameTime > (this.startTime + this.duration);
	}

	public boolean started(long gameTime) {
		return gameTime >= this.startTime;
	}

	public float calculateIntensityAt(Vec3 target, long gameTime, Optional<Vec3> optionalPosition) {
		if (this.expired(gameTime) || !this.started(gameTime)) {
			FrozenLibLogUtils.logError("Screen shake is expired or hasn't started yet!", FrozenLibLogUtils.UNSTABLE_LOGGING);
			return 0F;
		}

		if (optionalPosition.isEmpty()) {
			FrozenLibLogUtils.logError("Screen shake has no position!", FrozenLibLogUtils.UNSTABLE_LOGGING);
			return 0F;
		}

		final float relativeDistance = Math.max(1F - (Math.max((float) target.distanceTo(optionalPosition.get()), this.minDistance) / this.maxDistance), 0F);
		if (relativeDistance <= 0F) return 0F;

		final int ticks = Math.toIntExact(gameTime - this.startTime);
		float ticksUntilFalloffStart = Math.max(ticks - this.falloffStartDuration, 0); // Starts counting up once it reaches falloff start
		float falloffDuration = this.duration - this.falloffStartDuration; // The amount of time the intensity falls off for before reaching 0
		float timeRelativeTicksUntilFalloffStart = Mth.lerp((float) ticks / this.duration, 0, ticksUntilFalloffStart);

		return (relativeDistance * ((falloffDuration - timeRelativeTicksUntilFalloffStart) / falloffDuration)) * this.intensity;
	}

	public float calculateIntensityAt(Vec3 target, long gameTime) {
		return this.calculateIntensityAt(target, gameTime, this.position);
	}

	public float calculateIntensityAt(Vec3 target, long gameTime, Entity entity) {
		return this.calculateIntensityAt(target, gameTime, Optional.of(entity.position()));
	}

	public static class Builder {
		private final long startTime;
		private final Optional<Vec3> position;
		private float intensity = 1F;
		private float minDistance = 0F;
		private float maxDistance = 8F;
		private int duration = 20;
		private int falloffStartDuration = 5;

		private Builder(long startTime, Optional<Vec3> position) {
			this.startTime = startTime;
			this.position = position;
		}

		public Builder intensity(float intensity) {
			this.intensity = intensity;
			return this;
		}

		public Builder range(float minDistance, float maxDistance) {
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
			return this;
		}

		public Builder duration(int duration) {
			this.duration = duration;
			return this;
		}

		public Builder falloffStartDuration(int falloffStartDuration) {
			this.falloffStartDuration = falloffStartDuration;
			return this;
		}

		public ScreenShake build() {
			if (this.minDistance < 0F) throw new IllegalStateException("Screen shake min distance cannot be less than 0!");
			if (this.maxDistance <= 0F) throw new IllegalStateException("Screen shake max distance cannot be less than or equal to 0!");
			if (this.maxDistance < this.minDistance) throw new IllegalStateException("Screen shake max distance cannot be less than than min distance!");
			if (this.intensity <= 0F) throw new IllegalStateException("Screen shake intensity cannot be less than or equal to 0!");
			if (this.duration <= 0) throw new IllegalStateException("Screen shake duration cannot be less than or equal to 0!");
			return new ScreenShake(this.intensity, this.position, this.minDistance, this.maxDistance, this.startTime, this.duration, this.falloffStartDuration);
		}
	}
}
