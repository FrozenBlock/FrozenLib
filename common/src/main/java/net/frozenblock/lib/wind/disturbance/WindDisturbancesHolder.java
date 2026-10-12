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

package net.frozenblock.lib.wind.disturbance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentTarget;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;

public final class WindDisturbancesHolder implements Iterable<WindDisturbance<?, ?>> {
	private static final WindDisturbancesHolder EMPTY = new WindDisturbancesHolder();
	public static final DataAttachmentType<WindDisturbancesHolder> ATTACHMENT_TYPE = DataAttachmentType.create(
		FrozenLibConstants.id("wind_disturbances"),
		builder -> {}
	);
	private final List<WindDisturbance<?, ?>> disturbances = new ArrayList<>();

	private WindDisturbancesHolder() {}

	public WindDisturbancesHolder(List<WindDisturbance<?, ?>> disturbances) {
		this.disturbances.addAll(disturbances);
	}

	public static void init() {}

	public static void set(DataAttachmentTarget target, WindDisturbancesHolder disturbances) {
		ATTACHMENT_TYPE.set(target, disturbances);
	}

	public static void set(DataAttachmentTarget target, List<WindDisturbance<?, ?>> disturbances) {
		set(target, new WindDisturbancesHolder(disturbances));
	}

	public static void set(DataAttachmentTarget target, WindDisturbance<?, ?>... disturbances) {
		set(target, List.of(disturbances));
	}

	public static void add(DataAttachmentTarget target, WindDisturbance<?, ?> disturbance) {
		final WindDisturbancesHolder holder = get(target);
		if (holder.isEmpty()) {
			set(target, disturbance);
			return;
		}
		holder.add(disturbance);
	}

	public static void addIf(DataAttachmentTarget target, Predicate<DataAttachmentTarget> predicate, Supplier<WindDisturbance<?, ?>> disturbance) {
		if (!predicate.test(target)) return;
		add(target, disturbance.get());
	}

	public static void removeAttachment(DataAttachmentTarget target) {
		ATTACHMENT_TYPE.remove(target);
	}

	public static void removeIf(DataAttachmentTarget target, Predicate<WindDisturbance<?, ?>> removeIf) {
		final WindDisturbancesHolder holder = get(target);
		if (holder.isEmpty()) return;

		holder.removeIf(removeIf);
		if (holder.isEmpty()) removeAttachment(target);
	}

	public static boolean anyMatch(DataAttachmentTarget target, Predicate<WindDisturbance<?, ?>> predicate) {
		return get(target).anyMatch(predicate);
	}

	public static boolean allMatch(DataAttachmentTarget target, Predicate<WindDisturbance<?, ?>> predicate) {
		return get(target).allMatch(predicate);
	}

	public static boolean noneMatch(DataAttachmentTarget target, Predicate<WindDisturbance<?, ?>> predicate) {
		return get(target).noneMatch(predicate);
	}

	public static Predicate<WindDisturbance<?, ?>> type(WindDisturbance<?, ?> disturbance) {
		return other -> other.type().equals(disturbance.type());
	}

	public static Predicate<WindDisturbance<?, ?>> type(WindDisturbanceType<?> type) {
		return other -> other.type().equals(type);
	}

	public static boolean has(DataAttachmentTarget target) {
		return !get(target).isEmpty();
	}

	public static WindDisturbancesHolder get(DataAttachmentTarget target) {
		return ATTACHMENT_TYPE.getOrDefault(target, EMPTY);
	}

	public WindDisturbancesHolder add(WindDisturbance<?, ?> windDisturbanceType) {
		final List<WindDisturbance<?, ?>> newDisturbances = new ArrayList<>(this.disturbances);
		newDisturbances.add(windDisturbanceType);
		return new WindDisturbancesHolder(newDisturbances);
	}

	public WindDisturbancesHolder removeIf(Predicate<WindDisturbance<?, ?>> removeIf) {
		final List<WindDisturbance<?, ?>> newDisturbances = new ArrayList<>(this.disturbances);
		newDisturbances.removeIf(removeIf);
		return new WindDisturbancesHolder(newDisturbances);
	}

	public boolean anyMatch(Predicate<WindDisturbance<?, ?>> predicate) {
		return this.disturbances.stream().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<WindDisturbance<?, ?>> predicate) {
		return this.disturbances.stream().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<WindDisturbance<?, ?>> predicate) {
		return this.disturbances.stream().noneMatch(predicate);
	}

	public boolean isEmpty() {
		return this.disturbances.isEmpty();
	}

	@Override
	public Iterator<WindDisturbance<?, ?>> iterator() {
		return this.disturbances.iterator();
	}
}
