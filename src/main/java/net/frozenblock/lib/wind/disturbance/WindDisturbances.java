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
import java.util.function.Supplier;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.wind.WindManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;

public record WindDisturbances(List<WindDisturbance<?>> windDisturbances) implements Iterable<WindDisturbance<?>> {
	public static final WindDisturbances EMPTY = new WindDisturbances(List.of());
	public static final Codec<WindDisturbances> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WindDisturbance.LIST_CODEC.fieldOf("wind_disturbances").forGetter(WindDisturbances::windDisturbances)
	).apply(instance, WindDisturbances::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbances> STREAM_CODEC = StreamCodec.composite(
		WindDisturbance.LIST_STREAM_CODEC, WindDisturbances::windDisturbances,
		WindDisturbances::new
	);
	public static final AttachmentType<WindDisturbances> ATTACHMENT_TYPE = AttachmentRegistry.create(
		FrozenLibConstants.id("wind_disturbances"),
		builder -> {
			builder.persistent(CODEC);
			builder.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all());
		}
	);

	public static void init() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			WindManager.getOrCreate(level).trackOrUntrackDisturbanceHolder(entity);
			addIf(level, entity, isOfClassAndDoesntHaveDisturbance(Breeze.class, WindDisturbanceType.BREEZE), () -> BreezeWindDisturbance.INSTANCE);
			addIf(level, entity, isOfClassAndDoesntHaveDisturbance(AbstractWindCharge.class, WindDisturbanceType.WIND_CHARGE), () -> WindChargeWindDisturbance.INSTANCE);
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> WindManager.getOrCreate(level).untrackDisturbanceHolder(entity));

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).trackOrUntrackDisturbanceHolder(blockEntity));
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).untrackDisturbanceHolder(blockEntity));

		ServerChunkEvents.CHUNK_LOAD.register((serverLevel, chunk, generated) -> WindManager.getOrCreate(serverLevel).trackOrUntrackDisturbanceHolder(chunk));
		ServerChunkEvents.CHUNK_UNLOAD.register((serverLevel, chunk) -> WindManager.getOrCreate(serverLevel).untrackDisturbanceHolder(chunk));
	}

	public static void set(Level level, AttachmentTarget target, WindDisturbances windDisturbances) {
		target.setAttached(ATTACHMENT_TYPE, windDisturbances);
		WindManager.getOrCreate(level).trackDisturbanceHolder(target);
	}

	public static void set(Level level, AttachmentTarget target, List<WindDisturbance<?>> windDisturbances) {
		set(level, target, new WindDisturbances(windDisturbances));
	}

	public static void set(Level level, AttachmentTarget target, WindDisturbance<?>... windDisturbances) {
		set(level, target, List.of(windDisturbances));
	}

	public static void add(Level level, AttachmentTarget target, WindDisturbance<?> windDisturbance) {
		final WindDisturbances windDisturbances = get(target);
		if (windDisturbances.isEmpty()) {
			set(level, target, windDisturbance);
			return;
		}
		set(level, target, windDisturbances.add(windDisturbance));
	}

	public static void addIf(Level level, AttachmentTarget target, Predicate<AttachmentTarget> predicate, Supplier<WindDisturbance<?>> windDisturbance) {
		if (!predicate.test(target)) return;
		add(level, target, windDisturbance.get());
	}

	public static void removeAttachment(Level level, AttachmentTarget target) {
		target.removeAttached(ATTACHMENT_TYPE);
		WindManager.getOrCreate(level).untrackDisturbanceHolder(target);
	}

	public static void removeIf(Level level, AttachmentTarget target, Predicate<WindDisturbance<?>> removeIf) {
		final WindDisturbances windDisturbances = get(target);
		if (windDisturbances.isEmpty()) return;
		set(level, target, windDisturbances.removeIf(removeIf));
	}

	public static Predicate<AttachmentTarget> isOfClassAndDoesntHaveDisturbance(Class<?> clazz, WindDisturbanceType<?> type) {
		return target -> target.getClass().isAssignableFrom(clazz) && noneMatch(target, type(type));
	}

	public static boolean anyMatch(AttachmentTarget target, Predicate<WindDisturbance<?>> predicate) {
		return get(target).anyMatch(predicate);
	}

	public static boolean allMatch(AttachmentTarget target, Predicate<WindDisturbance<?>> predicate) {
		return  get(target).allMatch(predicate);
	}

	public static boolean noneMatch(AttachmentTarget target, Predicate<WindDisturbance<?>> predicate) {
		return  get(target).noneMatch(predicate);
	}

	public static Predicate<WindDisturbance<?>> type(WindDisturbanceType<?> type) {
		return windDisturbance -> windDisturbance.type().equals(type);
	}

	public static boolean has(AttachmentTarget target) {
		return ! get(target).isEmpty();
	}

	public static WindDisturbances get(AttachmentTarget target) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
	}

	public static Optional<Pair<AttachmentTarget, WindDisturbances>> getAsPair(AttachmentTarget target) {
		final WindDisturbances disturbances = get(target);
		return disturbances.isEmpty()
			? Optional.empty()
			: Optional.of(Pair.of(target, disturbances));
	}


	public WindDisturbances add(WindDisturbance<?> windDisturbance) {
		final List<WindDisturbance<?>> newDisturbances = new ArrayList<>(this.windDisturbances);
		newDisturbances.add(windDisturbance);
		return new WindDisturbances(newDisturbances);
	}

	public WindDisturbances removeIf(Predicate<WindDisturbance<?>> removeIf) {
		final List<WindDisturbance<?>> newDisturbances = new ArrayList<>(this.windDisturbances);
		newDisturbances.removeIf(removeIf);
		return new WindDisturbances(newDisturbances);
	}

	public boolean anyMatch(Predicate<WindDisturbance<?>> predicate) {
		return this.windDisturbances.stream().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<WindDisturbance<?>> predicate) {
		return this.windDisturbances.stream().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<WindDisturbance<?>> predicate) {
		return this.windDisturbances.stream().noneMatch(predicate);
	}

	public boolean isEmpty() {
		return this.windDisturbances.isEmpty();
	}

	@Override
	public Iterator<WindDisturbance<?>> iterator() {
		return this.windDisturbances().iterator();
	}
}
