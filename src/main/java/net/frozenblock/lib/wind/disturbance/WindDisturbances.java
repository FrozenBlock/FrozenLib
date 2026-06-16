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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.impl.networking.WindDisturbanceSyncPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;

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
		builder -> builder.persistent(CODEC)
	);
	public static void init() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			final WindManager windManager = WindManager.getOrCreate(level);
			windManager.reindexExisting(entity);
			windManager.addIfMissing(entity, isOfClassAndDoesntHaveDisturbance(Breeze.class, WindDisturbanceType.BREEZE), BreezeWindDisturbance.INSTANCE);
			windManager.addIfMissing(entity, isOfClassAndDoesntHaveDisturbance(AbstractWindCharge.class, WindDisturbanceType.WIND_CHARGE), WindChargeWindDisturbance.INSTANCE);
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> WindManager.getOrCreate(level).untrack(entity));

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).reindexExisting(blockEntity));
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).untrack(blockEntity));
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> WindManager.getOrCreate(level).reindexExisting(entity));
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> WindManager.getOrCreate(level).untrack(entity));

		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).reindexExisting(blockEntity));
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).untrack(blockEntity));
	}

	public static void set(AttachmentTarget target, WindDisturbance<?>... windDisturbances) {
		final WindDisturbances old = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (target instanceof Entity entity && entity.level() instanceof ServerLevel serverLevel) {
			for (WindDisturbance<?> oldDisturbance : old) {
				WindDisturbanceSyncPacket.sendToTracking(serverLevel, entity, oldDisturbance, false);
			}
		}
		target.setAttached(ATTACHMENT_TYPE, new WindDisturbances(List.of(windDisturbances)));
		if (target instanceof Entity entity && entity.level() instanceof ServerLevel serverLevel) {
			for (WindDisturbance<?> disturbance : windDisturbances) {
				WindDisturbanceSyncPacket.sendToTracking(serverLevel, entity, disturbance, true);
			}
		}
	}

	public static void add(AttachmentTarget target, WindDisturbance<?> windDisturbance) {
		final WindDisturbances windDisturbances = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (windDisturbances.isEmpty()) {
			set(target, windDisturbance);
			return;
		}
		target.setAttached(ATTACHMENT_TYPE, windDisturbances.add(windDisturbance));
		if (target instanceof Entity entity && entity.level() instanceof ServerLevel serverLevel) {
			WindDisturbanceSyncPacket.sendToTracking(serverLevel, entity, windDisturbance, true);
		}
	}

	public static void addIf(AttachmentTarget target, Predicate<AttachmentTarget> predicate, Supplier<WindDisturbance<?>> windDisturbance) {
		if (!predicate.test(target)) return;
		add(target, windDisturbance.get());
	}

	public static void removeAttachment(AttachmentTarget target) {
		final WindDisturbances old = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		target.removeAttached(ATTACHMENT_TYPE);
		if (target instanceof Entity entity && entity.level() instanceof ServerLevel serverLevel) {
			for (WindDisturbance<?> disturbance : old) {
				WindDisturbanceSyncPacket.sendToTracking(serverLevel, entity, disturbance, false);
			}
		}
	}

	public static void removeIf(AttachmentTarget target, Predicate<WindDisturbance<?>> removeIf) {
		final WindDisturbances windDisturbances = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (windDisturbances.isEmpty()) return;
		final List<WindDisturbance<?>> removed = windDisturbances.windDisturbances().stream().filter(removeIf).toList();
		target.setAttached(ATTACHMENT_TYPE, windDisturbances.removeIf(removeIf));
		if (target instanceof Entity entity && entity.level() instanceof ServerLevel serverLevel) {
			for (WindDisturbance<?> disturbance : removed) {
				WindDisturbanceSyncPacket.sendToTracking(serverLevel, entity, disturbance, false);
			}
		}
	}

	public static Predicate<AttachmentTarget> isOfClassAndDoesntHaveDisturbance(Class<?> clazz, WindDisturbanceType<?> type) {
		return target -> target.getClass().isAssignableFrom(clazz) && noneMatch(target, type(type));
	}

	public static boolean anyMatch(AttachmentTarget target, Predicate<WindDisturbance<?>> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).anyMatch(predicate);
	}

	public static boolean allMatch(AttachmentTarget target, Predicate<WindDisturbance<?>> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).allMatch(predicate);
	}

	public static boolean noneMatch(AttachmentTarget target, Predicate<WindDisturbance<?>> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).noneMatch(predicate);
	}

	public static Predicate<WindDisturbance<?>> type(WindDisturbanceType<?> type) {
		return windDisturbance -> windDisturbance.type().equals(type);
	}

	public static boolean has(AttachmentTarget target) {
		return !target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).isEmpty();
	}

	public static WindDisturbances get(AttachmentTarget target) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
	}

	public WindDisturbances add(WindDisturbance<?> windDisturbance) {
		final List<WindDisturbance<?>> newWindDisturbances = new ArrayList<>(this.windDisturbances);
		newWindDisturbances.add(windDisturbance);
		return new WindDisturbances(newWindDisturbances);
	}

	public WindDisturbances removeIf(Predicate<WindDisturbance<?>> removeIf) {
		final List<WindDisturbance<?>> newIcons = new ArrayList<>(this.windDisturbances);
		newIcons.removeIf(removeIf);
		return new WindDisturbances(newIcons);
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
