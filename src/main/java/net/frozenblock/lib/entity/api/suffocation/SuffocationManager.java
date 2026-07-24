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

package net.frozenblock.lib.entity.api.suffocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationData;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationStateInterface;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class SuffocationManager {
	private static final int DROWN_THRESHOLD = -20;
	private static final int BAR_BUBBLES = 10;

	private SuffocationManager() {}

	public static void applySource(LivingEntity entity, Holder<SuffocationType> type, int ticks) {
		if (entity.level().isClientSide()) return;
		((SuffocationStateInterface) entity).frozenLib$applySuffocationSource(type, ticks);
	}

	public static void serverTick(LivingEntity entity) {
		if (!(entity.level() instanceof ServerLevel level)) return;

		final Map<Holder<SuffocationType>, Integer> timers = ((SuffocationStateInterface) entity).frozenLib$suffocationSourceTimers();
		final Registry<SuffocationType> registry = SuffocationTypes.registry(level.registryAccess());

		final SuffocationData data = entity.getAttachedOrElse(SuffocationData.ATTACHMENT, SuffocationData.EMPTY);
		final Map<Holder<SuffocationType>, Integer> units = new HashMap<>(data.units());
		boolean changed = false;

		final BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());

		final boolean draining = isDraining(entity, level, registry, timers, eyePos);
		final boolean gasBarFull = gasBubbleCount(units) >= BAR_BUBBLES;

		for (SuffocationType type : registry) {
			final Holder<SuffocationType> holder = registry.wrapAsHolder(type);
			final boolean active = isSourceActive(level, eyePos, type, timers.getOrDefault(holder, 0) > 0);
			switch (type.mechanics().airBehavior()) {
				case DISPLAY_ONLY -> {}
				case DRAIN -> {
					if (type.mechanics().style() == MeterStyle.FILL) {
						final int direction;
						if (type.mechanics().pauseWhileDraining() && draining) direction = 0;
						else if (active) direction = gasBarFull ? 0 : 1;
						else direction = -1;
						changed |= tickLoad(holder, type, direction, units);
						if (active && gasBarFull
							&& entity.tickCount % Math.max(1, type.damageSettings().intervalTicks()) == 0) {
							entity.hurtServer(level, damageSource(level, type), type.damageSettings().amount());
						}
					} else {
						if (active) tickVanillaAirDrain(entity, level, type);
						changed |= tickLoad(holder, type, active ? 1 : -1, units);
					}
				}
				case NONE -> changed |= tickIndependentMeter(entity, level, holder, type, active, units);
			}
		}

		if (entity.isEyeInFluid(FluidTags.WATER)) capAirForGas(entity, units);

		final Iterator<Map.Entry<Holder<SuffocationType>, Integer>> it = timers.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<Holder<SuffocationType>, Integer> entry = it.next();
			final int remaining = entry.getValue() - 1;
			if (remaining <= 0) it.remove();
			else entry.setValue(remaining);
		}

		if (changed) entity.setAttached(SuffocationData.ATTACHMENT, new SuffocationData(units));
	}

	private static boolean isSourceActive(ServerLevel level, BlockPos eyePos, SuffocationType type, boolean timerActive) {
		if (timerActive) return true;
		return type.sourceBlocks().isPresent() && type.containsSourceBlock(level.getBlockState(eyePos));
	}

	public static boolean preventsVanillaAirRefill(LivingEntity entity) {
		if (!(entity.level() instanceof ServerLevel level)) return false;
		final Map<Holder<SuffocationType>, Integer> timers = ((SuffocationStateInterface) entity).frozenLib$suffocationSourceTimers();
		final Registry<SuffocationType> registry = SuffocationTypes.registry(level.registryAccess());
		final BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
		for (SuffocationType type : registry) {
			if (type.mechanics().airBehavior() != SuffocationType.AirBehavior.DRAIN) continue;
			if (type.mechanics().style() == MeterStyle.FILL) continue;
			final Holder<SuffocationType> holder = registry.wrapAsHolder(type);
			if (isSourceActive(level, eyePos, type, timers.getOrDefault(holder, 0) > 0)) return true;
		}
		return false;
	}

	private static void capAirForGas(LivingEntity entity, Map<Holder<SuffocationType>, Integer> units) {
		final int gasBubbles = gasBubbleCount(units);
		if (gasBubbles <= 0) return;
		final int maxAir = entity.getMaxAirSupply();
		final int ceiling = (BAR_BUBBLES - gasBubbles) * maxAir / BAR_BUBBLES;
		if (entity.getAirSupply() > ceiling) entity.setAirSupply(ceiling);
	}

	private static int gasBubbleCount(Map<Holder<SuffocationType>, Integer> units) {
		int bubbles = 0;
		for (Map.Entry<Holder<SuffocationType>, Integer> entry : units.entrySet()) {
			if (bubbles >= BAR_BUBBLES) break;
			final SuffocationType.Mechanics mechanics = entry.getKey().value().mechanics();
			if (mechanics.airBehavior().usesVanillaAir() && mechanics.style() == MeterStyle.FILL && mechanics.capacity() > 0) {
				final float fill = Mth.clamp((float) entry.getValue() / (float) mechanics.capacity(), 0F, 1F) * BAR_BUBBLES;
				bubbles += Mth.clamp(Mth.ceil(fill), 0, BAR_BUBBLES - bubbles);
			}
		}
		return bubbles;
	}

	private static void tickVanillaAirDrain(LivingEntity entity, ServerLevel level, SuffocationType type) {
		final int air = entity.getAirSupply();
		if (air <= DROWN_THRESHOLD) {
			entity.setAirSupply(0);
			entity.hurtServer(level, damageSource(level, type), type.damageSettings().amount());
		} else {
			final int step = Math.max(1, Math.abs(type.mechanics().dangerStep()));
			entity.setAirSupply(Math.max(DROWN_THRESHOLD, air - step));
		}
	}

	private static boolean tickLoad(Holder<SuffocationType> holder, SuffocationType type, int direction, Map<Holder<SuffocationType>, Integer> units) {
		if (direction == 0) return false;
		final SuffocationType.Mechanics mechanics = type.mechanics();
		final int capacity = mechanics.capacity();
		final int current = units.getOrDefault(holder, 0);
		final int step = direction > 0 ? perTick(capacity, mechanics.fillTime()) : -perTick(capacity, mechanics.drainTime());
		final int next = Mth.clamp(current + step, 0, capacity);
		if (next <= 0) return units.remove(holder) != null;
		return !Objects.equals(units.put(holder, next), next);
	}

	private static boolean isDraining(LivingEntity entity, ServerLevel level, Registry<SuffocationType> registry, Map<Holder<SuffocationType>, Integer> timers, BlockPos eyePos) {
		if (entity.isEyeInFluid(FluidTags.WATER)) return true;
		for (SuffocationType type : registry) {
			final SuffocationType.Mechanics m = type.mechanics();
			if (!m.airBehavior().usesVanillaAir() || m.style() == MeterStyle.FILL) continue;
			if (isSourceActive(level, eyePos, type, timers.getOrDefault(registry.wrapAsHolder(type), 0) > 0)) return true;
		}
		return false;
	}

	private static int perTick(int capacity, int time) {
		return time <= 0 ? capacity : Math.max(1, Math.round((float) capacity / (float) time));
	}

	private static boolean tickIndependentMeter(
		LivingEntity entity, ServerLevel level, Holder<SuffocationType> holder, SuffocationType type, boolean active,
		Map<Holder<SuffocationType>, Integer> units
	) {
		final SuffocationType.Mechanics mechanics = type.mechanics();
		final int capacity = mechanics.capacity();
		final int rest = mechanics.style().restValue(capacity);
		final int current = units.getOrDefault(holder, rest);
		final int next = Mth.clamp(current + (active ? mechanics.dangerStep() : mechanics.recoveryStep()), 0, capacity);

		if (active && mechanics.style().dangerFraction(next, capacity) >= 1F) {
			final int interval = Math.max(1, type.damageSettings().intervalTicks());
			if (entity.tickCount % interval == 0) entity.hurtServer(level, damageSource(level, type), type.damageSettings().amount());
		}

		if (next == rest) {
			return units.remove(holder) != null;
		}
		return !Objects.equals(units.put(holder, next), next);
	}

	private static DamageSource damageSource(ServerLevel level, SuffocationType type) {
		return type.damageSettings().damageType()
			.map(key -> level.damageSources().source(key))
			.orElse(level.damageSources().drown());
	}

	public static int getMeterUnits(LivingEntity entity, Holder<SuffocationType> type) {
		final SuffocationType.Mechanics mechanics = type.value().mechanics();
		final int rest = mechanics.style().restValue(mechanics.capacity());
		return entity.getAttachedOrElse(SuffocationData.ATTACHMENT, SuffocationData.EMPTY).getUnits(type, rest);
	}

	public static float dangerFraction(LivingEntity entity, Holder<SuffocationType> type) {
		final SuffocationType.Mechanics mechanics = type.value().mechanics();
		return mechanics.style().dangerFraction(getMeterUnits(entity, type), mechanics.capacity());
	}

	public static boolean isMeterActive(LivingEntity entity, Holder<SuffocationType> type) {
		final SuffocationType.Mechanics mechanics = type.value().mechanics();
		final int rest = mechanics.style().restValue(mechanics.capacity());
		return entity.getAttachedOrElse(SuffocationData.ATTACHMENT, SuffocationData.EMPTY).getUnits(type, rest) != rest;
	}
}
