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

package net.frozenblock.lib.config.v2.entry.predicates;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.ValidationContext;

/**
 * A re-implementation of {@link net.minecraft.client.renderer.item.properties.select.LocalTime LocalTime} in {@link ConfigPredicate} form.
 * @see net.minecraft.client.renderer.item.properties.select.LocalTime LocalTime
 */
public class LocalTimePredicate implements ConfigPredicate {
	private static final long UPDATE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1L);
	private static final Codec<TimeZone> TIME_ZONE_CODEC = Codec.STRING.comapFlatMap(string -> {
		final TimeZone timeZone = TimeZone.getTimeZone(string);
		return timeZone.equals(TimeZone.UNKNOWN_ZONE) ? DataResult.error(() -> "Unknown timezone: " + string) : DataResult.success(timeZone);
	}, TimeZone::getID);
	public static final MapCodec<LocalTimePredicate> CODEC = Data.MAP_CODEC.flatXmap(LocalTimePredicate::create, predicate -> DataResult.success(predicate.data));
	private final Data data;
	private final DateFormat parsedFormat;
	private long nextUpdateTimeMs;
	private String lastParsedDate = "";
	private boolean lastResult = false;

	private LocalTimePredicate(Data data, DateFormat parsedFormat) {
		this.data = data;
		this.parsedFormat = parsedFormat;
	}

	public static LocalTimePredicate create(String format, String localeId, Optional<TimeZone> timeZone, String... targetDates) {
		return create(format, localeId, timeZone, Arrays.stream(targetDates).toList());
	}

	public static LocalTimePredicate create(String format, String localeId, Optional<TimeZone> timeZone, List<String> targetDates) {
		return create(new Data(format, localeId, timeZone, targetDates))
			.getOrThrow(msg -> new IllegalStateException("Failed to validate format: " + msg));
	}

	private static DataResult<LocalTimePredicate> create(Data data) {
		final ULocale locale = new ULocale(data.localeId);
		final Calendar calendar = data.timeZone.map(timeZone -> Calendar.getInstance(timeZone, locale)).orElseGet(() -> Calendar.getInstance(locale));
		final SimpleDateFormat parsedFormat = new SimpleDateFormat(data.format, locale);
		parsedFormat.setCalendar(calendar);

		try {
			parsedFormat.format(new Date());
		} catch (Exception exception) {
			return DataResult.error(() -> "Invalid time format '" + parsedFormat + "': " + exception.getMessage());
		}

		return DataResult.success(new LocalTimePredicate(data, parsedFormat));
	}

	@Override
	public Boolean get() {
		final long currentTimeMs = Util.getMillis();
		if (currentTimeMs > this.nextUpdateTimeMs) {
			final String previousParsedDate = this.lastParsedDate;
			this.lastParsedDate = this.parseCurrentDate();
			this.nextUpdateTimeMs = currentTimeMs + UPDATE_INTERVAL_MS;
			if (!previousParsedDate.equals(this.lastParsedDate)) this.lastResult = this.data.targetDates.stream().anyMatch(date -> date.equals(this.lastParsedDate));
		}

		return this.lastResult;
	}

	private String parseCurrentDate() {
		return this.parsedFormat.format(new Date());
	}

	@Override
	public MapCodec<LocalTimePredicate> codec() {
		return CODEC;
	}

	@Override
	public void validate(ValidationContext context) {}

	private record Data(String format, String localeId, Optional<TimeZone> timeZone, List<String> targetDates) {
		public static final String ROOT_LOCALE = "";
		private static final MapCodec<Data> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("pattern").forGetter(Data::format),
			Codec.STRING.optionalFieldOf("locale", ROOT_LOCALE).forGetter(Data::localeId),
			TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter(Data::timeZone),
			Codec.STRING.listOf().fieldOf("target_dates").forGetter(Data::targetDates)
		).apply(instance, Data::new));
	}
}
