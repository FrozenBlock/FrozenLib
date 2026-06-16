package net.frozenblock.lib.wind.client;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

public class ClientWindUtil {

	@ApiStatus.Internal
	public static void init() {

	}

	@VisibleForDebug
	public static class Debug {
		private static final List<Vec3> ACCESSED_POSITIONS = new ArrayList<>();
		private static final List<WindDisturbance<?>> WIND_DISTURBANCES = new ArrayList<>();
		private static final List<List<Pair<Vec3, Integer>>> DEBUG_NODES = new ArrayList<>();
		private static final List<List<Pair<Vec3, Integer>>> DEBUG_DISTURBANCE_NODES = new ArrayList<>();

		public static void tick(ClientLevel level) {
			WIND_DISTURBANCES.clear();
			DEBUG_NODES.clear();
			DEBUG_DISTURBANCE_NODES.clear();

			if (FrozenLibConstants.DEBUG_WIND) DEBUG_NODES.addAll(createWindNodes(level));
			if (FrozenLibConstants.DEBUG_WIND_DISTURBANCES) {
				WIND_DISTURBANCES.addAll(getWindDisturbances());
				DEBUG_DISTURBANCE_NODES.addAll(createWindDisturbanceNodes(level));
			}

			ACCESSED_POSITIONS.clear();
		}

		@VisibleForDebug
		public static void addAccessedPosition(Vec3 vec3) {
			ACCESSED_POSITIONS.add(vec3);
		}

		@VisibleForDebug
		public static void clear() {
			ACCESSED_POSITIONS.clear();
			WIND_DISTURBANCES.clear();
			DEBUG_NODES.clear();
			DEBUG_DISTURBANCE_NODES.clear();
		}

		@VisibleForDebug
		public static List<List<Pair<Vec3, Integer>>> getDebugNodes() {
			return DEBUG_NODES;
		}

		private static List<List<Pair<Vec3, Integer>>> createWindNodes(ClientLevel level) {
			final List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
			ACCESSED_POSITIONS.forEach(
				vec3 -> {
					windNodes.add(createWindNodes(level, vec3, 1.5D, false));
				}
			);

			return windNodes;
		}

		@VisibleForDebug
		public static @Unmodifiable List<WindDisturbance<?>> getWindDisturbances() {
			return ImmutableList.copyOf(WIND_DISTURBANCES);
		}

		@VisibleForDebug
		public static List<List<Pair<Vec3, Integer>>> getDebugDisturbanceNodes() {
			return DEBUG_DISTURBANCE_NODES;
		}

		private static List<List<Pair<Vec3, Integer>>> createWindDisturbanceNodes(ClientLevel level) {
			final List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
			WIND_DISTURBANCES.forEach(
				windDisturbance -> {
					BlockPos.betweenClosed(
						BlockPos.containing(windDisturbance.affectedArea.getMinPosition()),
						BlockPos.containing(windDisturbance.affectedArea.getMaxPosition())
					).forEach(
						blockPos -> {
							final Vec3 blockPosCenter = Vec3.atCenterOf(blockPos);
							windNodes.add(createWindNodes(level, blockPosCenter, 1D, true));
						}
					);
				}
			);
			return windNodes;
		}

		private static List<Pair<Vec3, Integer>> createWindNodes(Level level, Vec3 origin, double stretch, boolean disturbanceOnly) {
			final List<Pair<Vec3, Integer>> windNodes = new ArrayList<>();
			Vec3 wind = disturbanceOnly ?
				ClientWindManager.getRawDisturbanceMovement(level, origin)
				: ClientWindManager.getWindMovement(level, origin);

			final double windLength = wind.length();
			if (windLength == 0D) return windNodes;

			int increments = 3;
			Vec3 lineStart = origin;
			double windLineScale = (1D / increments) * stretch;
			windNodes.add(
				Pair.of(
					lineStart,
					calculateNodeColor(Math.min(1D, windLength), disturbanceOnly)
				)
			);

			for (int i = 0; i < increments; ++i) {
				final Vec3 lineEnd = lineStart.add(wind.scale(windLineScale));
				windNodes.add(
					Pair.of(
						lineEnd,
						calculateNodeColor(Math.min(1D, windLength), disturbanceOnly)
					)
				);
				lineStart = lineEnd;
				wind = disturbanceOnly ?
					ClientWindManager.getRawDisturbanceMovement(level, lineStart)
					: ClientWindManager.getWindMovement(level, lineStart);
			}

			return windNodes;
		}

		private static int calculateNodeColor(double strength, boolean disturbanceOnly) {
			return ARGB.color(
				255,
				(int) Mth.lerp(strength, 255, 0),
				(int) Mth.lerp(strength, 90, 255),
				disturbanceOnly ? 0 : 255
			);
		}
	}
}
