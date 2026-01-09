/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.wind.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.frozenblock.lib.wind.impl.networking.WindSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;

/**
 * Handles wind on the server side.
 *
 * <p> One instance is created per {@link ServerLevel}.
 */
public class WindManager extends SavedData {
	public static final String WIND_FILE_PATH = "frozenlib_wind_";
	public static final String WIND_MANAGER_FILE_ID = WIND_FILE_PATH + "main";
	public static final Codec<WindManager> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			Codec.LONG.fieldOf("time").forGetter(windManager -> windManager.time),
			Codec.BOOL.fieldOf("overrideWind").forGetter(windManager -> windManager.overrideWind),
			Vec3.CODEC.fieldOf("commandWind").forGetter(windManager -> windManager.commandWind),
			Codec.DOUBLE.fieldOf("windX").forGetter(windManager -> windManager.windX),
			Codec.DOUBLE.fieldOf("windY").forGetter(windManager -> windManager.windY),
			Codec.DOUBLE.fieldOf("windZ").forGetter(windManager -> windManager.windZ),
			Codec.DOUBLE.fieldOf("laggedWindX").forGetter(windManager -> windManager.laggedWindX),
			Codec.DOUBLE.fieldOf("laggedWindY").forGetter(windManager -> windManager.laggedWindY),
			Codec.DOUBLE.fieldOf("laggedWindZ").forGetter(windManager -> windManager.laggedWindZ)
		).apply(instance, WindManager::createFromCodec)
	);
	public static final SavedDataType<WindManager> TYPE = new SavedDataType<>(WIND_MANAGER_FILE_ID, WindManager::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

	private static final long MIN_TIME_VALUE = Long.MIN_VALUE + 1;
	public static final Map<SavedDataType<? extends WindManagerExtension>, Integer> EXTENSION_PROVIDERS = new Object2ObjectOpenHashMap<>();
	private final List<WindDisturbance<?>> windDisturbancesA = new ArrayList<>();
	private final List<WindDisturbance<?>> windDisturbancesB = new ArrayList<>();
	private boolean isSwitchedServer;
	public List<WindManagerExtension> attachedExtensions;
	private boolean loadedExtensions;
	private ServerLevel level;
	public boolean overrideWind;
	public long time;
	public Vec3 commandWind = Vec3.ZERO;
	public double windX;
	public double windY;
	public double windZ;
	public double laggedWindX;
	public double laggedWindY;
	public double laggedWindZ;
	public long seed;
	private boolean seedSet = false;

	public ImprovedNoise noise = EasyNoiseSampler.createXoroNoise(this.seed);

	public WindManager() {
	}

	public static WindManager createFromCodec(
		long time,
		boolean overrideWind,
		Vec3 commandWind,
		double windX,
		double windY,
		double windZ,
		double laggedWindX,
		double laggedWindY,
		double laggedWindZ
	) {
		final WindManager windManager = new WindManager();
		windManager.time = time;
		windManager.overrideWind = overrideWind;
		windManager.commandWind = commandWind;
		windManager.windX = windX;
		windManager.windY = windY;
		windManager.windZ = windZ;
		windManager.laggedWindX = laggedWindX;
		windManager.laggedWindY = laggedWindY;
		windManager.laggedWindZ = laggedWindZ;

		return windManager;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public void setLevel(ServerLevel level) {
		this.level = level;
	}

	/**
	 * Adds a {@link WindManagerExtension}.
	 *
	 * @param savedDataType The {@link SavedDataType} of the added {@link WindManagerExtension}.
	 * @param priority The priority of the added {@link WindManagerExtension}. The lower the value, the earlier it will run.
	 */
	public static void addExtension(SavedDataType<? extends WindManagerExtension> savedDataType, int priority) {
		EXTENSION_PROVIDERS.put(savedDataType, priority);
	}

	/**
	 * Adds a {@link WindManagerExtension} with a priority of 1000.
	 *
	 * @param savedDataType The {@link SavedDataType} of the added {@link WindManagerExtension}.
	 */
	public static void addExtension(SavedDataType<? extends WindManagerExtension> savedDataType) {
		addExtension(savedDataType, 1000);
	}

	public void loadExtensionsIfNotLoaded(ServerLevel level) {
		if (this.loadedExtensions) return;

		final Map.Entry<SavedDataType<? extends WindManagerExtension>, Integer>[] extensionProviders = EXTENSION_PROVIDERS.entrySet().toArray(new Map.Entry[0]);
		Arrays.sort(extensionProviders, Map.Entry.comparingByValue());

		final DimensionDataStorage storage = level.getDataStorage();
		final List<WindManagerExtension> extensions = new ObjectArrayList<>();
		for (Map.Entry<SavedDataType<? extends WindManagerExtension>, Integer> extensionByPriority : extensionProviders) {
			SavedDataType<? extends WindManagerExtension> type = extensionByPriority.getKey();
			WindManagerExtension extension = storage.computeIfAbsent(type);
			extension.setWindManager(this);
			extensions.add(extension);
		}

		this.attachedExtensions = extensions;
		this.loadedExtensions = true;
	}

	/**
	 * Adds a {@link WindDisturbance} to the world and syncs if with the client if possible.
	 *
	 * @param windDisturbance The {@link WindDisturbance} to add to the world and send to the client.
	 */
	public void addWindDisturbanceAndSync(WindDisturbance<?> windDisturbance, ServerLevel level) {
		final Optional<WindDisturbancePacket> optionalPacket = windDisturbance.toPacket();
		if (optionalPacket.isPresent()) {
			for (ServerPlayer player : PlayerLookup.level(level)) {
				if (!windDisturbance.isWithinViewDistance(player.getChunkTrackingView())) continue;
				ServerPlayNetworking.send(player, optionalPacket.get());
			}
		}
		this.addWindDisturbance(windDisturbance);
	}

	/**
	 * Adds a {@link WindDisturbance} to the world.
	 *
	 * @param windDisturbance The {@link WindDisturbance} to add.
	 */
	public void addWindDisturbance(WindDisturbance<?> windDisturbance) {
		this.getWindDisturbanceStash().add(windDisturbance);
	}

	private List<WindDisturbance<?>> getWindDisturbances() {
		return !this.isSwitchedServer ? this.windDisturbancesA : this.windDisturbancesB;
	}

	private List<WindDisturbance<?>> getWindDisturbanceStash() {
		return this.isSwitchedServer ? this.windDisturbancesA : this.windDisturbancesB;
	}

	/**
	 * Clears all wind disturbances running on the current tick.
	 */
	public void clearWindDisturbances() {
		this.getWindDisturbances().clear();
	}

	/**
	 * Clears all wind disturbances running on the current tick, and the stash of wind disturbances to run the next tick.
	 */
	public void clearAllWindDisturbances() {
		this.getWindDisturbances().clear();
		this.getWindDisturbanceStash().clear();
	}

	/**
	 * Clears all wind disturbances running on the current tick, and replaces them with the stash of wind disturbances to run on the next tick.
	 */
	public void clearAndSwitchWindDisturbances() {
		this.clearWindDisturbances();
		this.isSwitchedServer = !this.isSwitchedServer;
	}

	/**
	 * Returns the {@link WindManager} used for a given {@link ServerLevel}.
	 *
	 * @param level The {@link ServerLevel} to obtain the {@link WindManager} for.
	 * @return the {@link WindManager} used for the given {@link ServerLevel}.
	 */
	public static WindManager getOrCreateWindManager(ServerLevel level) {
		final WindManager windManager = ((WindManagerInterface)level).frozenLib$getOrCreateWindManager();
		windManager.loadExtensionsIfNotLoaded(level);
		windManager.setLevel(level);
		return windManager;
	}

	public void tick(ServerLevel level) {
		if (!this.seedSet) {
			this.seedSet = true;
			this.seed = level.getSeed();
			this.noise = EasyNoiseSampler.createXoroNoise(this.seed);
		}

		if (!level.tickRateManager().runsNormally()) return;

		this.runResetsIfNeeded(level);

		this.time += 1;
		//WIND
		final float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		final double calcTime = this.time * 0.0005;
		final double calcTimeY = this.time * 0.00035;
		final Vec3 vec3 = sampleVec3(calcTime, calcTimeY, calcTime);
		this.windX = vec3.x + (vec3.x * thunderLevel);
		this.windY = vec3.y + (vec3.y * thunderLevel);
		this.windZ = vec3.z + (vec3.z * thunderLevel);
		//LAGGED WIND
		final double calcLaggedTime = (this.time - 40) * 0.0005;
		final double calcLaggedTimeY = (this.time - 60) * 0.00035;
		final Vec3 laggedVec = sampleVec3(calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		this.laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		this.laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		this.laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);

		//EXTENSIONS
		for (WindManagerExtension extension : this.attachedExtensions) {
			extension.baseTick(level);
			extension.tick(level);
		}

		//SYNC WITH CLIENTS IN CASE OF DESYNC
		if (this.time % 20 == 0) this.sendSync(level);
	}

	/**
	 * Resets the values in the rare case of an overflow.
	 *
	 * @return whether the values were reset this tick.
	 */
	private boolean runResetsIfNeeded(ServerLevel serverLevel) {
		boolean needsReset = false;
		if (Math.abs(this.time) == Long.MAX_VALUE) {
			needsReset = true;
			this.time = MIN_TIME_VALUE;
		}
		if (Math.abs(this.windX) == Double.MAX_VALUE) {
			needsReset = true;
			this.windX = 0;
		}
		if (Math.abs(this.windY) == Double.MAX_VALUE) {
			needsReset = true;
			this.windY = 0;
		}
		if (Math.abs(this.windZ) == Double.MAX_VALUE) {
			needsReset = true;
			this.windZ = 0;
		}
		if (Math.abs(this.laggedWindX) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindX = 0;
		}
		if (Math.abs(this.laggedWindY) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindY = 0;
		}
		if (Math.abs(this.laggedWindZ) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindZ = 0;
		}

		//EXTENSIONS
		for (WindManagerExtension extension : this.attachedExtensions) {
			if (extension.runResetsIfNeeded()) needsReset = true;
		}

		if (needsReset) this.sendSync(serverLevel);
		return needsReset;
	}

	public WindSyncPacket createSyncPacket() {
		return new WindSyncPacket(
			this.time,
			this.seed,
			this.overrideWind,
			this.commandWind
		);
	}

	public void sendSync(ServerLevel level) {
		final WindSyncPacket packet = this.createSyncPacket();
		for (ServerPlayer player : PlayerLookup.level(level)) this.sendSyncToPlayer(packet, player);
	}

	public void sendSyncToPlayer(WindSyncPacket packet, ServerPlayer player) {
		ServerPlayNetworking.send(player, packet);
		for (WindManagerExtension extension : this.attachedExtensions) ServerPlayNetworking.send(player, extension.syncPacket(packet));
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @return the wind movement at the center of the specified {@link BlockPos}.
	 */
	public Vec3 getWindMovement(BlockPos pos) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos));
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}, multiplied.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}, multiplied.
	 */
	public Vec3 getWindMovement(BlockPos pos, double scale) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), scale);
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}, multiplied and clamped.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}, multiplied and clamped.
	 */
	public Vec3 getWindMovement(BlockPos pos, double scale, double clamp) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), scale, clamp);
	}

	/**
	 * Returns the wind movement at the center of a specified {@link Vec3}.
	 *
	 * @param pos The {@link Vec3} to check.
	 * @return the wind movement at the specified {@link Vec3}.
	 */
	public Vec3 getWindMovement(Vec3 pos) {
		return this.getWindMovement(pos, 1D);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied.
	 *
	 * @param pos The {@link Vec3} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied.
	 */
	public Vec3 getWindMovement(Vec3 pos, double scale) {
		return this.getWindMovement(pos, scale, Double.MAX_VALUE);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied and clamped.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied and clamped.
	 */
	public Vec3 getWindMovement(Vec3 pos, double scale, double clamp) {
		return this.getWindMovement(pos, scale, clamp, 1D);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @param windDisturbanceScale Multiplies the wind disturbance value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 */
	public Vec3 getWindMovement(Vec3 pos, double scale, double clamp, double windDisturbanceScale) {
		final double brightness = this.level.getBrightness(LightLayer.SKY, BlockPos.containing(pos));
		final double windScale = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667D);
		final Pair<Double, Vec3> disturbance = this.calculateWindDisturbance(pos);
		final double disturbanceAmount = disturbance.getFirst();
		final Vec3 windDisturbance = disturbance.getSecond();
		final double windX = Mth.lerp(disturbanceAmount, this.windX * windScale, windDisturbance.x * windDisturbanceScale) * scale;
		final double windY = Mth.lerp(disturbanceAmount, this.windY * windScale, windDisturbance.y * windDisturbanceScale) * scale;
		final double windZ = Mth.lerp(disturbanceAmount, this.windZ * windScale, windDisturbance.z * windDisturbanceScale) * scale;

		if (FrozenLibConstants.DEBUG_WIND) FrozenNetworking.sendPacketToAllPlayers(this.level, new WindAccessPacket(pos));

		return new Vec3(
			Mth.clamp(windX, -clamp, clamp),
			Mth.clamp(windY, -clamp, clamp),
			Mth.clamp(windZ, -clamp, clamp)
		);
	}

	@Deprecated
	public Vec3 getWindMovement3D(BlockPos pos, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), stretch);
	}

	@Deprecated
	public Vec3 getWindMovement3D(BlockPos pos, double scale, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, stretch);
	}

	@Deprecated
	public Vec3 getWindMovement3D(BlockPos pos, double scale, double clamp, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, clamp, stretch);
	}

	@Deprecated
	public Vec3 getWindMovement3D(Vec3 pos, double stretch) {
		return this.getWindMovement3D(pos, 1D, stretch);
	}

	@Deprecated
	public Vec3 getWindMovement3D(Vec3 pos, double scale, double stretch) {
		return this.getWindMovement3D(pos, scale, Double.MAX_VALUE, stretch);
	}

	@Deprecated
	public Vec3 getWindMovement3D(Vec3 pos, double scale, double clamp, double stretch) {
		final Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3(Mth.clamp((wind.x()) * scale, -clamp, clamp),
				Mth.clamp((wind.y()) * scale, -clamp, clamp),
				Mth.clamp((wind.z()) * scale, -clamp, clamp));
	}

	private Vec3 sampleVec3(double x, double y, double z) {
		if (this.overrideWind) return this.commandWind;
		final double windX = this.noise.noise(x, 0D, 0D);
		final double windY = this.noise.noise(0D, y, 0D);
		final double windZ = this.noise.noise(0D, 0D, z);
		return new Vec3(windX, windY, windZ);
	}

	@Deprecated
	private Vec3 sample3D(Vec3 pos, double stretch) {
		final double sampledTime = this.time * 0.1D;
		final double xyz = pos.x() + pos.y() + pos.z();
		final double windX = this.noise.noise((xyz + sampledTime) * stretch, 0D, 0D);
		final double windY = this.noise.noise(0D, (xyz + sampledTime) * stretch, 0D);
		final double windZ = this.noise.noise(0D, 0D, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}

	/**
	 * Calculates the strength and movement of the current {@link WindDisturbance}s at a given position.
	 *
	 * @param pos The {@link Vec3} being checked.
	 * @return the strength and movement of the current {@link WindDisturbance}s at a given position.
	 */
	private Pair<Double, Vec3> calculateWindDisturbance(Vec3 pos) {
		return calculateWindDisturbance(this.getWindDisturbances(), this.level, pos);
	}

	/**
	 * Calculates the strength and movement out of a provided list of {@link WindDisturbance}s at a given position.
	 *
	 * @param windDisturbances The list of {@link WindDisturbance}s to calculate from.
	 * @param level The provided {@link Level}.
	 * @param pos The {@link Vec3} being checked.
	 * @return the strength and movement out of a provided list of {@link WindDisturbance}s at a given position.
	 */
	public static Pair<Double, Vec3> calculateWindDisturbance(List<WindDisturbance<?>> windDisturbances, Level level, Vec3 pos) {
		final ArrayList<Pair<Double, Vec3>> winds = new ArrayList<>();
		double strength = 0D;
		for (WindDisturbance<?> windDisturbance : windDisturbances) {
			final WindDisturbance.DisturbanceResult disturbanceResult = windDisturbance.calculateDisturbanceResult(level, pos);
			if (disturbanceResult.strength() <= 0D || disturbanceResult.weight() <= 0D) continue;

			strength = Math.max(strength, disturbanceResult.strength());
			winds.add(Pair.of(disturbanceResult.weight(), disturbanceResult.wind()));
		}

		double finalX = 0D;
		double finalY = 0D;
		double finalZ = 0D;
		if (!winds.isEmpty()) {
			double x = 0D;
			double y = 0D;
			double z = 0D;
			double sumOfWeights = 0D;
			for (Pair<Double, Vec3> pair : winds) {
				final double weight = pair.getFirst();
				sumOfWeights += weight;
				final Vec3 windVec = pair.getSecond();
				x += weight * windVec.x;
				y += weight * windVec.y;
				z += weight * windVec.z;
			}
			finalX = x / sumOfWeights;
			finalY = y / sumOfWeights;
			finalZ = z / sumOfWeights;
		}

		return Pair.of(strength, new Vec3(finalX, finalY, finalZ));
	}
}
