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

package net.frozenblock.lib.entity.client.impl.suffocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entity.api.suffocation.MeterStyle;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationData;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@ClientOnly
@UtilityClass
public final class ClientSuffocationState {
	private static final int BUBBLES = 10;
	private static int lastGasBubbles = 0;
	private static final Map<ResourceKey<SuffocationType>, Integer> previousUnits = new HashMap<>();
	private static final Set<ResourceKey<SuffocationType>> rising = new HashSet<>();

	public static void clientTick(LocalPlayer player) {
		final int gasBubbles = SuffocationBubbleRenderer.gasCount(player);
		if (gasBubbles > lastGasBubbles) {
			final SuffocationType loudest = dominantGas(player);
			if (loudest != null) loudest.sounds().fill().ifPresent(sound -> play(player, sound, 0.5F, 1F));
		}
		lastGasBubbles = gasBubbles;

		if (gasBubbles >= BUBBLES) {
			final SuffocationType loudest = dominantGas(player);
			if (loudest != null && player.tickCount % Math.max(1, loudest.damageSettings().intervalTicks()) == 0) {
				loudest.sounds().damage().ifPresent(sound -> play(player, sound, 0.7F, 1F));
			}
		}

		updateRising(player);
	}

	private static void updateRising(LocalPlayer player) {
		rising.clear();
		final Set<ResourceKey<SuffocationType>> present = new HashSet<>();
		for (Active a : active(player)) {
			final ResourceKey<SuffocationType> key = a.holder().unwrapKey().orElse(null);
			if (key == null) continue;
			present.add(key);
			if (a.units() > previousUnits.getOrDefault(key, 0)) rising.add(key);
			previousUnits.put(key, a.units());
		}
		previousUnits.keySet().retainAll(present);
	}

	public static boolean isRising(Holder<SuffocationType> holder) {
		return holder.unwrapKey().map(rising::contains).orElse(false);
	}

	private static SuffocationType dominantGas(Player player) {
		SuffocationType best = null;
		int bestUnits = -1;
		for (Active a : active(player)) {
			final SuffocationType.Mechanics m = a.type().mechanics();
			if (m.airBehavior().usesVanillaAir() && m.style() == MeterStyle.FILL && a.units() > bestUnits) {
				bestUnits = a.units();
				best = a.type();
			}
		}
		return best;
	}

	private static void play(LocalPlayer player, Holder<SoundEvent> sound, float volume, float pitch) {
		player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), sound.value(), SoundSource.PLAYERS, volume, pitch, false);
	}

	public record Active(Holder<SuffocationType> holder, SuffocationType type, int units, float danger) {}

	public static List<Active> active(LivingEntity entity) {
		final SuffocationData data = SuffocationData.ATTACHMENT.getAttachedOrElse(entity, SuffocationData.EMPTY);
		if (data.isEmpty()) return List.of();

		final List<Active> result = new ArrayList<>(data.units().size());
		data.units().forEach((holder, units) -> {
			final SuffocationType type = holder.value();
			final int capacity = type.mechanics().capacity();
			final float danger = type.mechanics().airBehavior().usesVanillaAir()
				? (capacity > 0 ? Mth.clamp((float) units / (float) capacity, 0F, 1F) : 0F)
				: type.mechanics().style().dangerFraction(units, capacity);
			result.add(new Active(holder, type, units, danger));
		});
		return result;
	}

	public static float[] dangers(List<Active> active) {
		final float[] dangers = new float[active.size()];
		for (int i = 0; i < active.size(); i++) dangers[i] = active.get(i).danger();
		return dangers;
	}

	public static void reset() {
		lastGasBubbles = 0;
		previousUnits.clear();
		rising.clear();
	}
}
