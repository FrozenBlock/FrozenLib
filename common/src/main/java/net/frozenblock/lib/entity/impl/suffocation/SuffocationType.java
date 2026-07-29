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

package net.frozenblock.lib.entity.impl.suffocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.entity.api.suffocation.AirBehavior;
import net.frozenblock.lib.entity.api.suffocation.MeterStyle;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// TODO: better field names
public record SuffocationType(
	Optional<HolderSet<Block>> sourceBlocks,
	Mechanics mechanics,
	DamageSettings damageSettings,
	Sounds sounds,
	MeterSettings meterSettings,
	Optional<ScreenEffectSettings> screenEffect
) {
	public static final Codec<SuffocationType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("source_blocks").forGetter(SuffocationType::sourceBlocks),
		Mechanics.CODEC.fieldOf("mechanics").forGetter(SuffocationType::mechanics),
		DamageSettings.CODEC.optionalFieldOf("damage_settings", DamageSettings.DEFAULT).forGetter(SuffocationType::damageSettings),
		Sounds.CODEC.optionalFieldOf("sounds", Sounds.EMPTY).forGetter(SuffocationType::sounds),
		MeterSettings.CODEC.fieldOf("meter").forGetter(SuffocationType::meterSettings),
		ScreenEffectSettings.CODEC.optionalFieldOf("screen_effect").forGetter(SuffocationType::screenEffect)
	).apply(instance, SuffocationType::new));
	public static final Codec<Holder<SuffocationType>> CODEC = RegistryFixedCodec.create(FrozenLibRegistries.SUFFOCATION_TYPE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SuffocationType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FrozenLibRegistries.SUFFOCATION_TYPE);

	public boolean containsSourceBlock(BlockState state) {
		return this.sourceBlocks.map(state::is).orElse(false);
	}

	public record Mechanics(MeterStyle style, AirBehavior airBehavior, int capacity, int fillTime, int drainTime, int priority, boolean pauseWhileDraining) {
		public static final Codec<Mechanics> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MeterStyle.CODEC.optionalFieldOf("style", MeterStyle.FILL).forGetter(Mechanics::style),
			AirBehavior.CODEC.optionalFieldOf("air_behavior", AirBehavior.NONE).forGetter(Mechanics::airBehavior),
			Codec.INT.optionalFieldOf("capacity", 300).forGetter(Mechanics::capacity),
			Codec.INT.fieldOf("fill_time").forGetter(Mechanics::fillTime),
			Codec.INT.fieldOf("drain_time").forGetter(Mechanics::drainTime),
			Codec.INT.optionalFieldOf("priority", 0).forGetter(Mechanics::priority),
			Codec.BOOL.optionalFieldOf("pause_while_draining", false).forGetter(Mechanics::pauseWhileDraining)
		).apply(instance, Mechanics::new));

		public int dangerStep() {
			final int time = this.style == MeterStyle.FILL ? this.fillTime : this.drainTime;
			final int step = Math.max(1, perTick(time));
			return this.style == MeterStyle.FILL ? step : -step;
		}

		public int recoveryStep() {
			final int time = this.style == MeterStyle.FILL ? this.drainTime : this.fillTime;
			final int step = Math.max(1, perTick(time));
			return this.style == MeterStyle.FILL ? -step : step;
		}

		private int perTick(int time) {
			return time <= 0 ? this.capacity : Math.max(1, Math.round((float) this.capacity / (float) time));
		}
	}

	public record DamageSettings(Optional<ResourceKey<DamageType>> damageType, float amount, int intervalTicks) {
		public static final DamageSettings DEFAULT = new DamageSettings(Optional.empty(), 2F, 20);
		public static final Codec<DamageSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceKey.codec(Registries.DAMAGE_TYPE).optionalFieldOf("damage_type").forGetter(DamageSettings::damageType),
			Codec.FLOAT.optionalFieldOf("amount", 2F).forGetter(DamageSettings::amount),
			Codec.INT.optionalFieldOf("interval_ticks", 20).forGetter(DamageSettings::intervalTicks)
		).apply(instance, DamageSettings::new));
	}

	public record Sounds(Optional<Holder<SoundEvent>> fill, Optional<Holder<SoundEvent>> drain, Optional<Holder<SoundEvent>> damage) {
		public static final Sounds EMPTY = new Sounds(Optional.empty(), Optional.empty(), Optional.empty());
		public static final Codec<Sounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SoundEvent.CODEC.optionalFieldOf("fill_sound").forGetter(Sounds::fill),
			SoundEvent.CODEC.optionalFieldOf("drain_sound").forGetter(Sounds::drain),
			SoundEvent.CODEC.optionalFieldOf("damage_sound").forGetter(Sounds::damage)
		).apply(instance, Sounds::new));
	}

	public record MeterSettings(Identifier full, Optional<Identifier> partial, Optional<Identifier> empty, Optional<Identifier> popping) {
		public static final Codec<MeterSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("full_texture").forGetter(MeterSettings::full),
			Identifier.CODEC.optionalFieldOf("partial_texture").forGetter(MeterSettings::partial),
			Identifier.CODEC.optionalFieldOf("empty_texture").forGetter(MeterSettings::empty),
			Identifier.CODEC.optionalFieldOf("popping_texture").forGetter(MeterSettings::popping)
		).apply(instance, MeterSettings::new));

		public Identifier partialOrFull() {
			return this.partial.orElse(this.full);
		}
	}

	public record Keyframe(float fraction, float value) {
		public static final Codec<Keyframe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("fraction").forGetter(Keyframe::fraction),
			Codec.FLOAT.fieldOf("value").forGetter(Keyframe::value)
		).apply(instance, Keyframe::new));
	}

	public enum RelativeMode implements StringRepresentable {
		ABSOLUTE("absolute"),
		RELATIVE_TO_TOTAL("relative_to_total"),
		DOMINANT_ONLY("dominant_only");
		public static final Codec<RelativeMode> CODEC = StringRepresentable.fromEnum(RelativeMode::values);
		private final String name;

		RelativeMode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

	public record ScreenEffectSettings(
		Optional<Identifier> overlayTexture,
		List<Keyframe> overlayAlphaCurve,
		Optional<Identifier> postEffect,
		List<Keyframe> postIntensityCurve,
		RelativeMode relativeMode
	) {
		public static final Codec<ScreenEffectSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("overlay_texture").forGetter(ScreenEffectSettings::overlayTexture),
			Keyframe.CODEC.listOf().optionalFieldOf("overlay_alpha_curve", List.of()).forGetter(ScreenEffectSettings::overlayAlphaCurve),
			Identifier.CODEC.optionalFieldOf("post_effect").forGetter(ScreenEffectSettings::postEffect),
			Keyframe.CODEC.listOf().optionalFieldOf("post_intensity_curve", List.of()).forGetter(ScreenEffectSettings::postIntensityCurve),
			RelativeMode.CODEC.optionalFieldOf("relative_mode", RelativeMode.ABSOLUTE).forGetter(ScreenEffectSettings::relativeMode)
		).apply(instance, ScreenEffectSettings::new));
	}

	public static Builder builder(MeterStyle style, int fillTime, int drainTime) {
		return new Builder(style, fillTime, drainTime);
	}

	public static final class Builder {
		private final MeterStyle style;
		private final int fillTime;
		private final int drainTime;
		private AirBehavior airBehavior = AirBehavior.NONE;
		private int capacity = 300;
		private int priority = 0;
		private boolean pauseWhileDraining = false;
		private HolderSet<Block> sourceBlocks = null;
		private DamageSettings damageSettings = DamageSettings.DEFAULT;
		private Sounds sounds = Sounds.EMPTY;
		private MeterSettings meterSettings;
		private ScreenEffectSettings screenEffect = null;

		private Builder(MeterStyle style, int fillTime, int drainTime) {
			this.style = style;
			this.fillTime = fillTime;
			this.drainTime = drainTime;
		}

		public Builder airBehavior(AirBehavior airBehavior) {
			this.airBehavior = airBehavior;
			return this;
		}

		public Builder capacity(int capacity) {
			this.capacity = capacity;
			return this;
		}

		public Builder priority(int priority) {
			this.priority = priority;
			return this;
		}

		public Builder pauseWhileDraining(boolean pauseWhileDraining) {
			this.pauseWhileDraining = pauseWhileDraining;
			return this;
		}

		public Builder sourceBlocks(HolderSet<Block> sourceBlocks) {
			this.sourceBlocks = sourceBlocks;
			return this;
		}

		public Builder damage(ResourceKey<DamageType> damageType, float amount, int intervalTicks) {
			this.damageSettings = new DamageSettings(Optional.ofNullable(damageType), amount, intervalTicks);
			return this;
		}

		public Builder sounds(Holder<SoundEvent> fill, Holder<SoundEvent> drain, Holder<SoundEvent> damage) {
			this.sounds = new Sounds(Optional.ofNullable(fill), Optional.ofNullable(drain), Optional.ofNullable(damage));
			return this;
		}

		public Builder meter(Identifier full, Identifier partial, Identifier empty, Identifier popping) {
			this.meterSettings = new MeterSettings(
				full, Optional.ofNullable(partial), Optional.ofNullable(empty), Optional.ofNullable(popping)
			);
			return this;
		}

		public Builder screenEffect(ScreenEffectSettings screenEffect) {
			this.screenEffect = screenEffect;
			return this;
		}

		public SuffocationType build() {
			if (this.meterSettings == null) throw new IllegalStateException("SuffocationType requires meter settings");
			return new SuffocationType(
				Optional.ofNullable(this.sourceBlocks),
				new Mechanics(this.style, this.airBehavior, this.capacity, this.fillTime, this.drainTime, this.priority, this.pauseWhileDraining),
				this.damageSettings,
				this.sounds,
				this.meterSettings,
				Optional.ofNullable(this.screenEffect)
			);
		}
	}
}
