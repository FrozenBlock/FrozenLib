/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.wind;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.wind.client.ClientWindUtil;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceResult;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.frozenblock.lib.wind.extension.WindManagerExtension;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Handles wind.
 *
 * <p> One instance is created per {@link Level}.
 */
public class WindManager {
	@ApiStatus.Internal
	public static final WindManager INSTANCE = new WindManager();
	public static final Codec<WindManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Vec3.CODEC.optionalFieldOf("wind_override").forGetter(windManager -> windManager.windOverride),
		Codec.DOUBLE.fieldOf("x").forGetter(windManager -> windManager.windX),
		Codec.DOUBLE.fieldOf("y").forGetter(windManager -> windManager.windY),
		Codec.DOUBLE.fieldOf("z").forGetter(windManager -> windManager.windZ),
		Codec.DOUBLE.fieldOf("lagged_x").forGetter(windManager -> windManager.laggedWindX),
		Codec.DOUBLE.fieldOf("lagged_y").forGetter(windManager -> windManager.laggedWindY),
		Codec.DOUBLE.fieldOf("lagged_z").forGetter(windManager -> windManager.laggedWindZ),
		WindManagerExtension.LIST_CODEC.fieldOf("extensions").forGetter(windManager -> windManager.extensions)
	).apply(instance, WindManager::createFromCodec));
	public static final StreamCodec<RegistryFriendlyByteBuf, WindManager> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Vec3.STREAM_CODEC), windManager -> windManager.windOverride,
		ByteBufCodecs.DOUBLE, windManager -> windManager.windX,
		ByteBufCodecs.DOUBLE, windManager -> windManager.windY,
		ByteBufCodecs.DOUBLE, windManager -> windManager.windZ,
		ByteBufCodecs.DOUBLE, windManager -> windManager.laggedWindX,
		ByteBufCodecs.DOUBLE, windManager -> windManager.laggedWindY,
		ByteBufCodecs.DOUBLE, windManager -> windManager.laggedWindZ,
		WindManagerExtension.LIST_STREAM_CODEC, windManager -> windManager.extensions,
		ByteBufCodecs.optional(ByteBufCodecs.LONG), windManager -> windManager.seed,
		WindManager::applyFromStreamCodec
	);
	public static final AttachmentType<WindManager> ATTACHMENT_TYPE = AttachmentRegistry.create(
		FrozenLibConstants.id("wind"),
		builder -> {
			builder.persistent(CODEC);
			builder.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all());
		}
	);

	private Level level;
	public final List<WindManagerExtension> extensions = new ArrayList<>();
	private final List<AttachmentTarget> disturbanceHolders = new ArrayList<>();
	private boolean loadedExtensions;
	public Optional<Vec3> windOverride = Optional.empty();
	public double windX;
	public double windY;
	public double windZ;
	public double laggedWindX;
	public double laggedWindY;
	public double laggedWindZ;
	private Optional<Long> seed = Optional.empty();
	public ImprovedNoise noise;

	private WindManager() {
		this.level = null;
	}

	private WindManager(ServerLevel level) {
		this.level = level;
		this.seed = Optional.of(RandomSource.create(level.getSeed()).nextLong());
	}

	private WindManager(Level level) {
		this.level = level;
	}

	public void setLevel(Level level) {
		this.level = level;
		if (level instanceof ServerLevel serverLevel && (this.seed.isEmpty() || this.noise == null)) {
			this.seed = Optional.of(RandomSource.create(serverLevel.getSeed()).nextLong());
			this.trySync(serverLevel);
		}
	}

	public static void init() {}

	/**
	 * Returns the {@link WindManager} used for a given {@link Level}.
	 *
	 * @param level The {@link Level} to obtain the {@link WindManager} for.
	 * @return the {@link WindManager} used for the given {@link Level}.
	 */
	public static WindManager getOrCreate(Level level) {
		WindManager windManager = level instanceof ServerLevel
			? level.getAttached(ATTACHMENT_TYPE)
			: INSTANCE;
		if (windManager == null) {
			windManager = new WindManager((ServerLevel) level);
			level.setAttached(ATTACHMENT_TYPE, windManager);
		} else {
			windManager.setLevel(level);
		}
		windManager.tryCreateAndSortExtensions(level);
		return windManager;
	}

	@ApiStatus.Internal
	private static WindManager createFromCodec(
		Optional<Vec3> windOverride,
		double windX, double windY, double windZ,
		double laggedWindX, double laggedWindY, double laggedWindZ,
		List<WindManagerExtension> extensions
	) {
		final WindManager windManager = new WindManager();
		windManager.windOverride = windOverride;
		windManager.windX = windX;
		windManager.windY = windY;
		windManager.windZ = windZ;
		windManager.laggedWindX = laggedWindX;
		windManager.laggedWindY = laggedWindY;
		windManager.laggedWindZ = laggedWindZ;
		windManager.extensions.addAll(extensions);
		return windManager;
	}

	@ApiStatus.Internal
	private static WindManager applyFromStreamCodec(
		Optional<Vec3> windOverride,
		double windX, double windY, double windZ,
		double laggedWindX, double laggedWindY, double laggedWindZ,
		List<WindManagerExtension> extensions,
		Optional<Long> seed
	) {
		INSTANCE.windOverride = windOverride;
		INSTANCE.windX = windX;
		INSTANCE.windY = windY;
		INSTANCE.windZ = windZ;
		INSTANCE.laggedWindX = laggedWindX;
		INSTANCE.laggedWindY = laggedWindY;
		INSTANCE.laggedWindZ = laggedWindZ;
		for (WindManagerExtension syncedExtension : extensions) {
			final WindManagerExtension existingExtension = INSTANCE.extensions.stream()
				.filter(existing -> existing.type() == syncedExtension.type())
				.findFirst()
				.orElse(null);

			if (existingExtension == null) {
				INSTANCE.extensions.add(syncedExtension);
			} else {
				existingExtension.applyFromSyncedInstance(syncedExtension);
			}
		}
		INSTANCE.seed = seed;
		return INSTANCE;
	}

	@ApiStatus.Internal
	private void tryCreateAndSortExtensions(Level level) {
		if (this.loadedExtensions) return;

		final List<WindManagerExtension> extensions = new ArrayList<>(this.extensions);
		level.registryAccess().lookupOrThrow(FrozenLibRegistries.WIND_MANAGER_EXTENSION_TYPE_REGISTRY).stream()
			.filter(type -> extensions.stream().noneMatch(extension -> extension.type() == type))
			.forEach(type -> extensions.add(type.supplier().get()));

		this.extensions.clear();
		extensions.stream()
			.sorted(Comparator.comparingInt(e -> e.type().priority()))
			.forEach(this.extensions::add);

		this.loadedExtensions = true;
	}

	/**
	 * @return if this has a seed present. If empty, this is expected to be on a client connected to a server without FrozenLib.
	 */
	public boolean usable() {
		return this.seed.isPresent();
	}

	/**
	 * Returns the {@link WindManagerExtension} of the given {@link WindManagerExtensionType}, if available.
	 * @param type The {@link WindManagerExtensionType} to get.
	 * @return the extension of the given type, if available.
	 */
	public <T extends WindManagerExtension> Optional<T> getExtension(WindManagerExtensionType<T> type) {
		return (Optional<T>) this.extensions.stream().filter(extension -> extension.type() == type).findFirst();
	}

	/**
	 * Returns the {@link WindManagerExtension} of the given {@link WindManagerExtensionType} used for a given {@link Level}, if available.
	 * @param level The {@link Level} to obtain the {@link WindManagerExtension} for.
	 * @param type The {@link WindManagerExtensionType} to get.
	 * @return the extension of the given type, if available.
	 */
	public static <T extends WindManagerExtension> Optional<T> getExtension(Level level, WindManagerExtensionType<T> type) {
		return WindManager.getOrCreate(level).getExtension(type);
	}

	/**
	 * @return the currently tracked {@link WindDisturbances}, paired with their respective sources in {@link AttachmentTarget} sources.
	 */
	public List<Pair<AttachmentTarget, WindDisturbances>> getWindDisturbances() {
		return this.disturbanceHolders.stream()
			.map(target -> WindDisturbances.getAsPair(target))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();
	}

	/**
	 * Tracks an {@link AttachmentTarget}.
	 * <p>
	 * This is used to track {@link WindDisturbances}.
	 *
	 * @param target The {@link AttachmentTarget} to be tracked.
	 */
	public void trackDisturbanceHolder(@Nullable AttachmentTarget target) {
		if (target == null || this.disturbanceHolders.contains(target)) return;
		this.disturbanceHolders.add(target);
	}

	/**
	 * Untracks an {@link AttachmentTarget}.
	 *
	 * @param target The {@link AttachmentTarget} to be untracked.
	 */
	public void untrackDisturbanceHolder(AttachmentTarget target) {
		this.disturbanceHolders.remove(target);
	}

	/**
	 * Tracks or untracks a {@link AttachmentTarget} depending on whether it is present and has {@link WindDisturbances}.
	 *
	 * @param target The {@link AttachmentTarget} to be tracked or untracked.
	 */
	public void trackOrUntrackDisturbanceHolder(@Nullable AttachmentTarget target) {
		if (target == null) return;
		if (WindDisturbances.has(target)) {
			trackDisturbanceHolder(target);
		} else {
			untrackDisturbanceHolder(target);
		}
	}

	/**
	 * Resets all values to their default state.
	 * <p>
	 * Should only be used for {@link WindManager#INSTANCE} upon exiting a {@link Level}.
	 */
	public void reset() {
		this.level = null;
		this.extensions.clear();
		this.disturbanceHolders.clear();
		this.windOverride = Optional.empty();
		this.windX = 0D;
		this.windY = 0D;
		this.windZ = 0D;
		this.laggedWindX = 0D;
		this.laggedWindY = 0D;
		this.laggedWindZ = 0D;
		this.seed = Optional.empty();
		this.noise = null;
	}

	public void tick(Level level) {
		this.disturbanceHolders.removeIf(target -> {
			if (target == null) return true;
			WindDisturbances.removeIf(level, target, disturbance -> disturbance.invalidOrExpired(target, level));
			return !WindDisturbances.has(target);
		});
		if (!level.tickRateManager().runsNormally()) return;

		if (this.seed.isEmpty()) return;
		if (this.noise == null) this.noise = EasyNoiseSampler.createXoroNoise(this.seed.get());

		final long time = level.getGameTime();
		this.runResetsIfNeeded(level);

		// Tick wind
		final float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		final double calcTime = time * 0.0005;
		final double calcTimeY = time * 0.00035;
		final Vec3 vec3 = sampleAt(calcTime, calcTimeY, calcTime);
		this.windX = vec3.x + (vec3.x * thunderLevel);
		this.windY = vec3.y + (vec3.y * thunderLevel);
		this.windZ = vec3.z + (vec3.z * thunderLevel);
		// Tick lagged wind
		final double calcLaggedTime = (time - 40) * 0.0005;
		final double calcLaggedTimeY = (time - 60) * 0.00035;
		final Vec3 laggedVec = sampleAt(calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		this.laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		this.laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		this.laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);

		// Tick extensions
		for (WindManagerExtension extension : List.copyOf(this.extensions)) {
			extension.baseTick(this, level);
			extension.tick(this, level);
		}

		// Sync with clients every 40 ticks
		if (time % 40 == 0) this.trySync(level);
	}

	/**
	 * Resets values in the rare case of an overflow.
	 *
	 * @return whether any values were reset.
	 */
	private boolean runResetsIfNeeded(Level level) {
		boolean needsReset = false;
		if (Math.abs(this.windX) == Double.MAX_VALUE) {
			needsReset = true;
			this.windX = 0D;
		}
		if (Math.abs(this.windY) == Double.MAX_VALUE) {
			needsReset = true;
			this.windY = 0D;
		}
		if (Math.abs(this.windZ) == Double.MAX_VALUE) {
			needsReset = true;
			this.windZ = 0D;
		}
		if (Math.abs(this.laggedWindX) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindX = 0D;
		}
		if (Math.abs(this.laggedWindY) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindY = 0D;
		}
		if (Math.abs(this.laggedWindZ) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindZ = 0D;
		}

		//EXTENSIONS
		for (WindManagerExtension extension : this.extensions) {
			if (extension.runResetsIfNeeded()) needsReset = true;
		}

		if (needsReset) this.trySync(level);
		return needsReset;
	}

	public void trySync(Level level) {
		if (!(level instanceof ServerLevel serverLevel)) return;

		final AttachmentChange syncedAttachment = new AttachmentChange(AttachmentTargetInfo.LevelTarget.INSTANCE, ATTACHMENT_TYPE, this);
		for (ServerPlayer player : PlayerLookup.level(serverLevel)) AttachmentSync.trySync(syncedAttachment, player);
	}

	/**
	 * Returns the wind movement at the bottom center of a given position.
	 *
	 * @param target The {@link BlockPos} to check.
	 * @return the wind movement at the center of a given position.
	 */
	public Vec3 getWindMovement(BlockPos target) {
		return this.getWindMovement(Vec3.atBottomCenterOf(target));
	}

	/**
	 * Returns the wind movement at the bottom center of a given position, multiplied.
	 *
	 * @param target The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the bottom center of a given position, multiplied.
	 */
	public Vec3 getWindMovement(BlockPos target, double scale) {
		return this.getWindMovement(Vec3.atBottomCenterOf(target), scale);
	}

	/**
	 * Returns the wind movement at the bottom center of a given position, multiplied and clamped.
	 *
	 * @param target The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the bottom center of a given position, multiplied and clamped.
	 */
	public Vec3 getWindMovement(BlockPos target, double scale, double clamp) {
		return this.getWindMovement(Vec3.atBottomCenterOf(target), scale, clamp);
	}

	/**
	 * Returns the wind movement at the center of a given position.
	 *
	 * @param target The {@link Vec3} to check.
	 * @return the wind movement at a given position.
	 */
	public Vec3 getWindMovement(Vec3 target) {
		return this.getWindMovement(target, 1D);
	}

	/**
	 * Returns the wind movement at a given position, multiplied.
	 *
	 * @param target The {@link Vec3} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at a given position, multiplied.
	 */
	public Vec3 getWindMovement(Vec3 target, double scale) {
		return this.getWindMovement(target, scale, Double.MAX_VALUE);
	}

	/**
	 * Returns the wind movement at a given position, multiplied and clamped.
	 *
	 * @param target The {@link Vec3} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at a given position, multiplied and clamped.
	 */
	public Vec3 getWindMovement(Vec3 target, double scale, double clamp) {
		return this.getWindMovement(target, scale, clamp, 1D);
	}

	/**
	 * Returns the wind movement at a given position, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 *
	 * @param target The {@link Vec3} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @param windDisturbanceScale Multiplies the wind disturbance value.
	 * @return the wind movement at a given position, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 */
	public Vec3 getWindMovement(Vec3 target, double scale, double clamp, double windDisturbanceScale) {
		if (!this.usable()) return Vec3.ZERO;
		final double brightness = this.level.getBrightness(LightLayer.SKY, BlockPos.containing(target));
		final double windScale = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667D);
		final Pair<Double, Vec3> disturbance = this.calculateWindDisturbance(target);
		final double disturbanceAmount = disturbance.getFirst();
		final Vec3 windDisturbance = disturbance.getSecond();
		final double windX = Mth.lerp(disturbanceAmount, this.windX * windScale, windDisturbance.x * windDisturbanceScale) * scale;
		final double windY = Mth.lerp(disturbanceAmount, this.windY * windScale, windDisturbance.y * windDisturbanceScale) * scale;
		final double windZ = Mth.lerp(disturbanceAmount, this.windZ * windScale, windDisturbance.z * windDisturbanceScale) * scale;

		if (FrozenLibConstants.DEBUG_WIND && this.level instanceof ServerLevel serverLevel) {
			FrozenNetworking.sendPacketToAllPlayers(serverLevel, new WindAccessPacket(target));
		} else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientWindUtil.Debug.addAccessedPosition(target);
		}

		return new Vec3(
			Mth.clamp(windX, -clamp, clamp),
			Mth.clamp(windY, -clamp, clamp),
			Mth.clamp(windZ, -clamp, clamp)
		);
	}

	private Vec3 sampleAt(double x, double y, double z) {
		if (!this.usable()) return Vec3.ZERO;
		if (this.windOverride.isPresent()) return this.windOverride.get();
		final double windX = this.noise.noise(x, 0D, 0D);
		final double windY = this.noise.noise(0D, y, 0D);
		final double windZ = this.noise.noise(0D, 0D, z);
		return new Vec3(windX, windY, windZ);
	}

	/**
	 * Calculates the strength and movement of the current {@link WindDisturbances} entries at a given position.
	 * <p>
	 * Expired or invalidated entries are pruned from the list (and their persisted attachment) as they're encountered,
	 * instead of being swept in a separate per-tick pass.
	 *
	 * @param target The {@link Vec3} to check.
	 * @return the strength and movement of the current disturbances at the given position.
	 */
	private Pair<Double, Vec3> calculateWindDisturbance(Vec3 target) {
		final ArrayList<WindDisturbanceResult.Success> successes = new ArrayList<>();
		double maxStrength = 0D;
		for (Pair<AttachmentTarget, WindDisturbances> tracked : this.getWindDisturbances()) {
			final AttachmentTarget source = tracked.getFirst();
			for (WindDisturbance disturbance : tracked.getSecond()) {
				if (disturbance.expired(source, this.level)) continue;

				final WindDisturbanceResult result = disturbance.get(source, this.level, target);
				if (!(result instanceof WindDisturbanceResult.Success success)) continue;
				if (success.strength() <= 0D || success.weight() <= 0D) continue;

				maxStrength = Math.max(maxStrength, success.strength());
				successes.add(success);
			}
		}

		double finalX = 0D;
		double finalY = 0D;
		double finalZ = 0D;
		if (!successes.isEmpty()) {
			double x = 0D;
			double y = 0D;
			double z = 0D;
			double sumOfWeights = 0D;
			for (WindDisturbanceResult.Success success : successes) {
				final double weight = success.weight();
				sumOfWeights += weight;
				final Vec3 vector = success.vector();
				x += weight * vector.x;
				y += weight * vector.y;
				z += weight * vector.z;
			}
			finalX = x / sumOfWeights;
			finalY = y / sumOfWeights;
			finalZ = z / sumOfWeights;
		}

		return Pair.of(maxStrength, new Vec3(finalX, finalY, finalZ));
	}

	/**
	 * Returns the wind disturbance contribution at a given position, ignoring the base wind.
	 *
	 * @param target The {@link Vec3} to check.
	 * @return the movement contributed by {@link WindDisturbance}s at the given position.
	 */
	public Vec3 getRawDisturbanceMovement(Vec3 target) {
		if (!this.usable()) return Vec3.ZERO;
		return this.calculateWindDisturbance(target).getSecond();
	}
}
