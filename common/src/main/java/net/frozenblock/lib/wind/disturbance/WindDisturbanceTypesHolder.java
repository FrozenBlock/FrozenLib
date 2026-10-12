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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.event.api.events.BlockEntityLifecycleEvents;
import net.frozenblock.lib.event.api.events.ChunkLifecycleEvents;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentTarget;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;
import net.frozenblock.lib.wind.WindManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;

public record WindDisturbanceTypesHolder(List<WindDisturbanceType<?>> types) implements Iterable<WindDisturbanceType<?>> {
	public static final WindDisturbanceTypesHolder EMPTY = new WindDisturbanceTypesHolder(List.of());
	public static final Codec<WindDisturbanceTypesHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WindDisturbanceType.LIST_CODEC.fieldOf("types").forGetter(WindDisturbanceTypesHolder::types)
	).apply(instance, WindDisturbanceTypesHolder::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbanceTypesHolder> STREAM_CODEC = StreamCodec.composite(
		WindDisturbanceType.LIST_STREAM_CODEC, WindDisturbanceTypesHolder::types,
		WindDisturbanceTypesHolder::new
	);
	public static final DataAttachmentType<WindDisturbanceTypesHolder> ATTACHMENT_TYPE = DataAttachmentType.create(
		FrozenLibConstants.id("wind_disturbance_types"),
		builder -> builder.persistent(CODEC).syncWith(STREAM_CODEC)
	);

	public static void init() {
		EntityLifecycleEvents.ENTITY_LOAD.register((entity, level) -> {
			WindManager.getOrCreate(level).trackOrUntrackDisturbanceHolder(entity);
			addIf(level, entity, isOfClassAndDoesntHaveDisturbance(Breeze.class, WindDisturbanceTypes.BREEZE.get()), BreezeWindDisturbanceType.INSTANCE);
			addIf(level, entity, isOfClassAndDoesntHaveDisturbance(AbstractWindCharge.class, WindDisturbanceTypes.WIND_CHARGE.get()), WindChargeWindDisturbanceType.INSTANCE);
		});
		EntityLifecycleEvents.ENTITY_UNLOAD.register((entity, level) -> WindManager.getOrCreate(level).untrackDisturbanceHolder(entity));

		BlockEntityLifecycleEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).trackOrUntrackDisturbanceHolder(blockEntity));
		BlockEntityLifecycleEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).untrackDisturbanceHolder(blockEntity));

		ChunkLifecycleEvents.CHUNK_LOAD.register((serverLevel, chunk, generated) -> WindManager.getOrCreate(serverLevel).trackOrUntrackDisturbanceHolder(chunk));
		ChunkLifecycleEvents.CHUNK_UNLOAD.register((serverLevel, chunk) -> WindManager.getOrCreate(serverLevel).untrackDisturbanceHolder(chunk));
	}

	public static void mirrorToDisturbancesHolder(Level level, DataAttachmentTarget target) {
		get(target).forEach(type -> {
			WindDisturbancesHolder.removeIf(target, WindDisturbancesHolder.type(type));

			WindDisturbancesHolder.addIf(
				target,
				t -> WindDisturbancesHolder.noneMatch(t, WindDisturbancesHolder.type(type)),
				() -> new WindDisturbance(level, target, type)
			);
		});
	}

	public static void set(Level level, DataAttachmentTarget target, WindDisturbanceTypesHolder windDisturbanceTypesHolder) {
		ATTACHMENT_TYPE.set(target, windDisturbanceTypesHolder);
		WindManager.getOrCreate(level).trackDisturbanceHolder(target);
	}

	public static void set(Level level, DataAttachmentTarget target, List<WindDisturbanceType<?>> windDisturbanceTypes) {
		set(level, target, new WindDisturbanceTypesHolder(windDisturbanceTypes));
	}

	public static void set(Level level, DataAttachmentTarget target, WindDisturbanceType<?>... windDisturbanceTypes) {
		set(level, target, List.of(windDisturbanceTypes));
	}

	public static void add(Level level, DataAttachmentTarget target, WindDisturbanceType<?> windDisturbanceType) {
		final WindDisturbanceTypesHolder holder = get(target);
		if (holder.isEmpty()) {
			set(level, target, windDisturbanceType);
			return;
		}
		set(level, target, holder.add(windDisturbanceType));
	}

	public static void addIf(Level level, DataAttachmentTarget target, Predicate<DataAttachmentTarget> predicate, WindDisturbanceType<?> type) {
		if (!predicate.test(target)) return;
		add(level, target, type);
	}

	public static void removeAttachment(Level level, DataAttachmentTarget target, boolean notifyWindManager) {
		ATTACHMENT_TYPE.remove(target);
		if (notifyWindManager) WindManager.getOrCreate(level).untrackDisturbanceHolder(target);
	}

	public static void removeIf(Level level, DataAttachmentTarget target, Predicate<WindDisturbanceType<?>> removeIf, boolean notifyWindManager) {
		final WindDisturbanceTypesHolder holder = get(target);
		if (holder.isEmpty()) return;

		final WindDisturbanceTypesHolder newHolder = holder.removeIf(removeIf);
		if (newHolder.isEmpty()) removeAttachment(level, target, notifyWindManager);
	}

	public static Predicate<DataAttachmentTarget> isOfClassAndDoesntHaveDisturbance(Class<?> clazz, WindDisturbanceType<?> type) {
		return target -> clazz.isAssignableFrom(target.getClass()) && noneMatch(target, type(type));
	}

	public static boolean anyMatch(DataAttachmentTarget target, Predicate<WindDisturbanceType<?>> predicate) {
		return get(target).anyMatch(predicate);
	}

	public static boolean allMatch(DataAttachmentTarget target, Predicate<WindDisturbanceType<?>> predicate) {
		return get(target).allMatch(predicate);
	}

	public static boolean noneMatch(DataAttachmentTarget target, Predicate<WindDisturbanceType<?>> predicate) {
		return get(target).noneMatch(predicate);
	}

	public static Predicate<WindDisturbanceType<?>> type(WindDisturbanceType<?> type) {
		return windDisturbance -> windDisturbance.equals(type);
	}

	public static boolean has(DataAttachmentTarget target) {
		return !get(target).isEmpty();
	}

	public static WindDisturbanceTypesHolder get(DataAttachmentTarget target) {
		return ATTACHMENT_TYPE.getOrDefault(target, EMPTY);
	}

	public static Optional<Pair<DataAttachmentTarget, WindDisturbanceTypesHolder>> getAsPair(DataAttachmentTarget target) {
		final WindDisturbanceTypesHolder disturbances = get(target);
		return disturbances.isEmpty()
			? Optional.empty()
			: Optional.of(Pair.of(target, disturbances));
	}

	public WindDisturbanceTypesHolder add(WindDisturbanceType<?> windDisturbanceType) {
		final List<WindDisturbanceType<?>> newDisturbances = new ArrayList<>(this.types);
		newDisturbances.add(windDisturbanceType);
		return new WindDisturbanceTypesHolder(newDisturbances);
	}

	public WindDisturbanceTypesHolder removeIf(Predicate<WindDisturbanceType<?>> removeIf) {
		final List<WindDisturbanceType<?>> newDisturbances = new ArrayList<>(this.types);
		newDisturbances.removeIf(removeIf);
		return new WindDisturbanceTypesHolder(newDisturbances);
	}

	public boolean anyMatch(Predicate<WindDisturbanceType<?>> predicate) {
		return this.types.stream().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<WindDisturbanceType<?>> predicate) {
		return this.types.stream().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<WindDisturbanceType<?>> predicate) {
		return this.types.stream().noneMatch(predicate);
	}

	public boolean isEmpty() {
		return this.types.isEmpty();
	}

	@Override
	public Iterator<WindDisturbanceType<?>> iterator() {
		return this.types().iterator();
	}
}
