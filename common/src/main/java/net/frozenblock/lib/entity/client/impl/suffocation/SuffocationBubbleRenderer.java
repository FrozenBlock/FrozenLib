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
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entity.api.suffocation.MeterStyle;
import net.frozenblock.lib.entity.api.suffocation.SuffocationTypes;
import net.frozenblock.lib.entity.client.impl.suffocation.ClientSuffocationState.Active;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType.MeterSettings;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.gui.Hud;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@ClientOnly
@UtilityClass
public final class SuffocationBubbleRenderer {

	private static final Comparator<Active> GAS_ORDER = Comparator
		.comparingInt((Active a) -> a.type().mechanics().priority()).reversed()
		.thenComparing(a -> a.holder().unwrapKey().map(key -> key.identifier().toString()).orElse(""));

	public static boolean hasHazard(Player player) {
		return !gasHazards(player).isEmpty();
	}

	public static boolean underwater(Player player) {
		return player.isEyeInFluid(FluidTags.WATER);
	}

	public static int gasCount(Player player) {
		int total = 0;
		for (Segment segment : segments(player)) total += segment.count();
		return total;
	}

	public static int freeAirBubbles(Player player) {
		return Hud.NUM_AIR_BUBBLES - gasCount(player);
	}

	public static int waterAirBubbles(Player player) {
		final int maxAir = Math.max(1, player.getMaxAirSupply());
		final int air = Mth.clamp(player.getAirSupply(), 0, maxAir);
		return Mth.clamp(Mth.ceil((float) ((air - 2) * Hud.NUM_AIR_BUBBLES) / (float) maxAir), 0, Hud.NUM_AIR_BUBBLES);
	}

	public static Identifier airSprite(Player player, int airBubble, Identifier vanilla) {
		final Identifier gas = gasSpriteAt(player, airBubble);
		if (gas != null) return gas;
		final MeterSettings skin = activeSkin(player);
		return skin != null ? skin.full() : vanilla;
	}

	public static Identifier emptySprite(Player player, int airBubble, Identifier vanilla) {
		final Identifier gas = gasSpriteAt(player, airBubble);
		if (gas != null) return gas;
		final MeterSettings skin = activeSkin(player);
		return skin != null ? skin.empty().orElse(vanilla) : vanilla;
	}

	public static Identifier poppingSprite(Player player, int airBubble, Identifier vanilla) {
		final Identifier gas = gasSpriteAt(player, airBubble);
		if (gas != null) return gas;
		final MeterSettings skin = activeSkin(player);
		return skin != null ? skin.popping().orElse(vanilla) : vanilla;
	}

	private static MeterSettings activeSkin(Player player) {
		final Level level = player.level();
		final Registry<SuffocationType> registry = SuffocationTypes.registry(level.registryAccess());
		final BlockState eye = level.getBlockState(BlockPos.containing(player.getX(), player.getEyeY(), player.getZ()));
		for (SuffocationType type : registry) {
			final SuffocationType.Mechanics m = type.mechanics();
			if (!m.airBehavior().usesVanillaAir() || m.style() == MeterStyle.FILL || type.sourceBlocks().isEmpty()) continue;
			if (type.containsSourceBlock(eye)) return type.meterSettings();
		}
		return null;
	}

	private record Segment(SuffocationType type, int count, float leadFraction, boolean rising) {}

	private static List<Segment> segments(Player player) {
		final List<Segment> out = new ArrayList<>();
		int used = 0;
		for (Active a : gasHazards(player)) {
			if (used >= Hud.NUM_AIR_BUBBLES) break;
			final int capacity = Math.max(1, a.type().mechanics().capacity());
			final float fill = Mth.clamp((float) a.units() / (float) capacity, 0F, 1F) * Hud.NUM_AIR_BUBBLES;
			final int count = Mth.clamp(Mth.ceil(fill), 0, Hud.NUM_AIR_BUBBLES - used);
			if (count <= 0) continue;
			out.add(new Segment(a.type(), count, Math.min(fill, count) - (count - 1), ClientSuffocationState.isRising(a.holder())));
			used += count;
		}
		return out;
	}

	private static Identifier gasSpriteAt(Player player, int airBubble) {
		final int fromLeft = Hud.NUM_AIR_BUBBLES - airBubble;
		int acc = 0;
		for (Segment segment : segments(player)) {
			if (fromLeft < acc + segment.count()) {
				final MeterSettings meter = segment.type().meterSettings();
				final boolean leading = fromLeft == acc + segment.count() - 1;
				final boolean transitioning = leading && segment.rising() && segment.leadFraction() <= fillWindow(segment.type());
				return transitioning ? meter.partialOrFull() : meter.full();
			}
			acc += segment.count();
		}
		return null;
	}

	private static float fillWindow(SuffocationType type) {
		final int capacity = Math.max(1, type.mechanics().capacity());
		final int fillTime = Math.max(1, type.mechanics().fillTime());
		final int loadStep = Math.max(1, Math.round((float) capacity / (float) fillTime));
		return (float) Hud.AIR_BUBBLE_POPPING_DURATION * loadStep / capacity * Hud.NUM_AIR_BUBBLES;
	}

	private static List<Active> gasHazards(Player player) {
		final List<Active> out = new ArrayList<>();
		for (Active a : ClientSuffocationState.active(player)) {
			final SuffocationType.Mechanics m = a.type().mechanics();
			if (m.airBehavior().usesVanillaAir() && m.style() == MeterStyle.FILL && a.units() > 0) out.add(a);
		}
		out.sort(GAS_ORDER);
		return out;
	}
}
