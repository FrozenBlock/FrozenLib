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

package net.frozenblock.lib.config.api.client.option;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public final class Option<T> {

	public static final Enum<Boolean> BOOLEAN_VALUES = new Enum<>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
	private static final int TOOLTIP_WIDTH = 200;

	private final OptionInstance.TooltipSupplier<T> tooltip;
	final Function<T, Component> toString;
	private final ValueSet<T> values;
	private final Codec<T> codec;
	private final T initialValue;
	private final Consumer<T> onValueUpdate;
	final Component caption;
	T value;

	public Option(
			Component caption,
			OptionInstance.TooltipSupplier<T> tooltip,
			CaptionBasedToString<T> textGetter,
			ValueSet<T> values,
			T initialValue,
			Consumer<T> onValueUpdate
	) {
		this(caption, tooltip, textGetter, values, values.codec(), initialValue, onValueUpdate);
	}

	public Option(
			Component caption,
			OptionInstance.TooltipSupplier<T> tooltip,
			CaptionBasedToString<T> textGetter,
			ValueSet<T> values,
			Codec<T> codec,
			T initialValue,
			Consumer<T> onValueUpdate
	) {
		this.caption = caption;
		this.tooltip = tooltip;
		this.toString = val -> textGetter.toString(this.caption, val);
		this.values = values;
		this.codec = codec;
		this.initialValue = initialValue;
		this.onValueUpdate = onValueUpdate;
		this.value = this.initialValue;
	}

	public static Component pixelValueLabel(Component optionText, int value) {
		return Component.translatable("options.pixel_value", optionText, value);
	}

	public static Component percentValueLabel(Component optionText, double value) {
		return Component.translatable("options.percent_value", optionText, (int)(value * 100.0));
	}

	public static <T> OptionInstance.TooltipSupplier<T> noTooltip() {
		return value -> ImmutableList.of();
	}

	public static <T> OptionInstance.TooltipSupplier<T> cachedConstantTooltip(Component tooltip) {
		List<FormattedCharSequence> list = splitTooltip(Minecraft.getInstance(), tooltip);
		return value -> list;
	}

	public static <T extends OptionEnum> CaptionBasedToString<T> forOptionEnum() {
		return (optionText, value) -> value.getCaption();
	}

	private static List<FormattedCharSequence> splitTooltip(Minecraft minecraft, Component tooltip) {
		return minecraft.font.split(tooltip, TOOLTIP_WIDTH);
	}

	public AbstractWidget createButton(Config<?> config, int x, int y, int width) {
		return this.values.createButton(this.tooltip, config, x, y, width).apply(this);
	}

	public T get() {
		return this.value;
	}

	public Codec<T> codec() {
		return this.codec;
	}

	public String toString() {
		return this.caption.getString();
	}

	public void set(T value) {
		this.value = this.values.validateValue(value).orElse(this.initialValue);
	}

	public ValueSet<T> values() {
		return this.values;
	}

	public Consumer<T> onValueUpdate() {
		return this.onValueUpdate;
	}

	@SuppressWarnings("unchecked")
	public static <T> ValueSet<T> get(T value) {
		if (value instanceof Number) {
			return (ValueSet<T>) new IntRange(0, 100);
		} else if (value instanceof Boolean) {
			return (ValueSet<T>) BOOLEAN_VALUES;
		}
		return null;
	}

	public record AltEnum<T>(
			List<T> values, List<T> altValues, BooleanSupplier altCondition, CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec
	) implements CycleableValueSet<T> {

		@Override
		public CycleButton.ValueListSupplier<T> valueListSupplier() {
			return CycleButton.ValueListSupplier.create(this.altCondition, this.values, this.altValues);
		}

		@Override
		public Optional<T> validateValue(T value) {
			var valueList = this.altCondition.getAsBoolean() ? this.altValues : this.values;

			return valueList.contains(value) ? Optional.of(value) : Optional.empty();
		}
	}

	public interface CaptionBasedToString<T> {
		Component toString(Component component, T object);
	}

	public record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier)
		implements IntRangeBase,
		SliderableOrCyclableValueSet<Integer> {

		@Override
		public Optional<Integer> validateValue(Integer value) {
			return Optional.of(Mth.clamp(value, this.minInclusive(), this.maxInclusive()));
		}

		@Override
		public int maxInclusive() {
			return this.maxSupplier.getAsInt();
		}

		@Override
		public Codec<Integer> codec() {
			Function<Integer, DataResult<Integer>> function = val -> {
				int i = this.maxInclusive() + 1;
				return val.compareTo(this.minInclusive()) >= 0 && val.compareTo(i) <= 0
						? DataResult.success(val)
						: DataResult.error("Value " + val + " is outside of range [" + this.minInclusive() + ":" + i + "]", val);
			};
			return Codec.INT.flatXmap(function, function);
		}

		@Override
		public boolean createCycleButton() {
			return true;
		}

		@Override
		public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
			return CycleButton.ValueListSupplier.create(IntStream.range(this.minInclusive(), this.maxInclusive() + 1).boxed().toList());
		}
	}

	interface CycleableValueSet<T> extends ValueSet<T> {
		CycleButton.ValueListSupplier<T> valueListSupplier();

		default ValueSetter<T> valueSetter() {
			return Option::set;
		}

		@Override
		default Function<Option<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> tooltip, Config<?> config, int x, int y, int width) {
			return option -> CycleButton.builder(option.toString)
					.withValues(this.valueListSupplier())
					.withTooltip(tooltip)
					.withInitialValue(option.value)
					.create(x, y, width, 20, option.caption, (button, value) ->
						this.valueSetter().set(option, value)
					);
		}

		@FunctionalInterface
		interface ValueSetter<T> {
			void set(Option<T> option, T object);
		}
	}

	public record Enum<T>(List<T> values, Codec<T> codec) implements CycleableValueSet<T> {
		@Override
		public Optional<T> validateValue(T value) {
			return this.values.contains(value) ? Optional.of(value) : Optional.empty();
		}

		@Override
		public CycleButton.ValueListSupplier<T> valueListSupplier() {
			return CycleButton.ValueListSupplier.create(this.values);
		}
	}

	public record IntRange(int minInclusive, int maxInclusive) implements IntRangeBase {
		public Optional<Integer> validateValue(Integer value) {
			return value.compareTo(this.minInclusive()) >= 0 && value.compareTo(this.maxInclusive()) <= 0 ? Optional.of(value) : Optional.empty();
		}

		@Override
		public Codec<Integer> codec() {
			return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
		}
	}

	interface IntRangeBase extends SliderableValueSet<Integer> {
		int minInclusive();

		int maxInclusive();

		default double toSliderValue(Integer value) {
			return Mth.map(value, this.minInclusive(), this.maxInclusive(), 0.0F, 1.0F);
		}

		default Integer fromSliderValue(double d) {
			return Mth.floor(Mth.map(d, 0.0, 1.0, this.minInclusive(), this.maxInclusive()));
		}

		default <R> SliderableValueSet<R> xmap(IntFunction<? extends R> fromSliderValue, ToIntFunction<? super R> toSliderValue) {
			return new SliderableValueSet<>() {
				@Override
				public Optional<R> validateValue(R value) {
					return IntRangeBase.this.validateValue(toSliderValue.applyAsInt(value)).map(fromSliderValue::apply);
				}

				@Override
				public double toSliderValue(R value) {
					return IntRangeBase.this.toSliderValue(toSliderValue.applyAsInt(value));
				}

				@Override
				public R fromSliderValue(double value) {
					return fromSliderValue.apply(IntRangeBase.this.fromSliderValue(value));
				}

				@Override
				public Codec<R> codec() {
					return IntRangeBase.this.codec().xmap(fromSliderValue::apply, toSliderValue::applyAsInt);
				}
			};
		}
	}

	public record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec)
		implements CycleableValueSet<T>{

		@Override
		public Optional<T> validateValue(T value) {
			return this.validateValue.apply(value);
		}

		@Override
		public CycleButton.ValueListSupplier<T> valueListSupplier() {
			return CycleButton.ValueListSupplier.create(this.values.get());
		}
	}

	static final class OptionSliderButton<N> extends AbstractOptionSliderButton implements TooltipAccessor {
		private final Option<N> option;
		private final SliderableValueSet<N> values;
		private final OptionInstance.TooltipSupplier<N> tooltip;

		OptionSliderButton(
				Config<?> config,
				int x,
				int y,
				int width,
				int height,
				Option<N> option,
				SliderableValueSet<N> values,
				OptionInstance.TooltipSupplier<N> tooltip
		) {
			super(config, x, y, width, height, values.toSliderValue(option.get()));
			this.option = option;
			this.values = values;
			this.tooltip = tooltip;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(this.option.toString.apply(this.option.get()));
		}

		@Override
		protected void applyValue() {
			this.option.set(this.values.fromSliderValue(this.value));
		}

		@Override
		@NotNull
		public List<FormattedCharSequence> getTooltip() {
			return this.tooltip.apply(this.values.fromSliderValue(this.value));
		}
	}

	interface SliderableOrCyclableValueSet<T> extends CycleableValueSet<T>, SliderableValueSet<T> {
		boolean createCycleButton();

		@Override
		default Function<Option<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> tooltip, Config<?> config, int x, int y, int width) {
			return this.createCycleButton()
					? CycleableValueSet.super.createButton(tooltip, config, x, y, width)
					: SliderableValueSet.super.createButton(tooltip, config, x, y, width);
		}
	}

	interface SliderableValueSet<T> extends ValueSet<T> {
		double toSliderValue(T value);

		T fromSliderValue(double value);

		@Override
		default Function<Option<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> tooltip, Config<?> config, int x, int y, int width) {
			return option -> new OptionSliderButton<>(config, x, y, width, 20, option, this, tooltip);
		}
	}

	public enum UnitDouble implements SliderableValueSet<Double> {
		INSTANCE;

		@Override
		public Optional<Double> validateValue(Double value) {
			return value >= 0.0 && value <= 1.0 ? Optional.of(value) : Optional.empty();
		}

		@Override
		public double toSliderValue(Double value) {
			return value;
		}

		@Override
		public Double fromSliderValue(double value) {
			return value;
		}

		public <R> SliderableValueSet<R> xmap(DoubleFunction<? extends R> fromSliderValue, ToDoubleFunction<? super R> toSliderValue) {
			return new SliderableValueSet<R>() {
				@Override
				public Optional<R> validateValue(R value) {
					return UnitDouble.this.validateValue(toSliderValue.applyAsDouble(value)).map(fromSliderValue::apply);
				}

				@Override
				public double toSliderValue(R value) {
					return UnitDouble.this.toSliderValue(toSliderValue.applyAsDouble(value));
				}

				@Override
				public R fromSliderValue(double value) {
					return fromSliderValue.apply(UnitDouble.this.fromSliderValue(value));
				}

				@Override
				public Codec<R> codec() {
					return UnitDouble.this.codec().xmap(fromSliderValue::apply, toSliderValue::applyAsDouble);
				}
			};
		}

		@Override
		public Codec<Double> codec() {
			return Codec.either(Codec.doubleRange(0.0, 1.0), Codec.BOOL).xmap(either -> either.map(val -> val, max -> Boolean.TRUE.equals(max) ? 1.0 : 0.0), Either::left);
		}
	}

	interface ValueSet<T> {
		Function<Option<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> tooltip, Config<?> config, int x, int y, int width);

		Optional<T> validateValue(T value);

		Codec<T> codec();
	}

	public static Option<Boolean> createBoolean(Component caption, boolean initialValue, Consumer<Boolean> onValueUpdate) {
		return createBoolean(caption, noTooltip(), initialValue, onValueUpdate);
	}

	public static Option<Boolean> createBoolean(Component caption, boolean initialValue) {
		return createBoolean(caption, noTooltip(), initialValue);
	}

	public static Option<Boolean> createBoolean(Component caption, OptionInstance.TooltipSupplier<Boolean> tooltip, boolean initialValue) {
		return createBoolean(caption, tooltip, initialValue, value -> {
		});
	}

	public static Option<Boolean> createBoolean(
			Component caption, OptionInstance.TooltipSupplier<Boolean> tooltip, boolean initialValue, Consumer<Boolean> onValueUpdate
	) {
		return new Option<>(
				caption,
				tooltip,
				(caption1, value) -> Boolean.TRUE.equals(value) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF,
				BOOLEAN_VALUES,
				initialValue,
				onValueUpdate
		);
	}

	public static Option<Integer> createIntSlider(
			Component caption, OptionInstance.TooltipSupplier<Integer> tooltip, int min, int max, int initialValue, Consumer<Integer> onValueUpdate
	) {
		return new Option<>(
				caption,
				tooltip,
				Option::percentValueLabel,
				new Option.IntRange(0, 100),
				Codec.intRange(min, max),
				initialValue,
				onValueUpdate
		);
	}
}
