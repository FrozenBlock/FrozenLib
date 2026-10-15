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
import java.util.List;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.registry.FrozenLibBuiltInRegistries;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class WindDisturbance<T> {
	public static final Codec<WindDisturbance<?>> CODEC = FrozenLibBuiltInRegistries.WIND_DISTURBANCE_TYPE
		.byNameCodec()
		.dispatch(WindDisturbance::getType, WindDisturbanceType::codec);
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbance<?>> STREAM_CODEC = ByteBufCodecs.registry(FrozenLibRegistries.WIND_DISTURBANCE_TYPE)
		.dispatch(WindDisturbance::getType, WindDisturbanceType::streamCodec);

	/**
	 * Provides the scale of a {@link WindDisturbance}, with 1 being the default.
	 * <p>
	 * While this is seldom used, it can be useful where the {@code source} can have multiple
	 * sizes (i.e., the {@link LivingEntity}'s {@link Attributes#SCALE scale attribute}.)
	 *
	 * @param source The source of the {@link WindDisturbance}.
	 * @param level The current {@link Level}.
	 * @return The scale of the {@link WindDisturbance}.
	 */
	public double scale(T source, Level level) {
		return 1D;
	}

	/**
	 * Provides the {@link Vec3 origin position} of a {@link WindDisturbance}.
	 *
	 * @param source The source of the {@link WindDisturbance}.
	 * @param level The current {@link Level}.
	 * @return The {@link Vec3 origin position} of the {@link WindDisturbance}.
	 */
	public abstract Vec3 origin(T source, Level level);

	/**
	 * Provides the {@link AABB area} a {@link WindDisturbance} is effective within.
	 *
	 * @param source The source of the {@link WindDisturbance}.
	 * @param level The current {@link Level}.
	 * @param origin The {origin @link Vec3 position} of the {@link WindDisturbance}.
	 * @param scale The scale of the {@link WindDisturbance}.
	 * @return The {@link AABB area} the {@link WindDisturbance} is effective within.
	 */
	public abstract AABB area(T source, Level level, Vec3 origin, double scale);

	/**
	 * Computes the {@link WindDisturbanceResult result} of a {@link WindDisturbance}, either {@link WindDisturbanceResult#PASS passing} if it isn't
	 * effective, or returning its {@link WindDisturbanceResult#success(double, double, Vec3) strength, relative weight, and vector} if it is
	 * effective at the given {@link Vec3 target position}.
	 * <p>
	 * It isn't required to implement a check for the effective {@link AABB area} in this method, as
	 * it is handled by {@link Instance} automatically.
	 *
	 * @param source The source of the {@link WindDisturbance}.
	 * @param level The current {@link Level}.
	 * @param origin The {@link Vec3 origin position} of the {@link WindDisturbance}.
	 * @param area The {@link AABB area} the {@link WindDisturbance} is effective within.
	 * @param target The {@link Vec3 target position} to evaluate the {@link WindDisturbance}'s {@link WindDisturbanceResult result} at.
	 * @param scale The scale of the {@link WindDisturbance}.
	 * @return The {@link WindDisturbanceResult result} of the {@link WindDisturbance}.
	 */
	public abstract WindDisturbanceResult sample(T source, Level level, Vec3 origin, AABB area, Vec3 target, double scale);

	/**
	 * Determines whether a {@link WindDisturbance} is expired, or can continue being used.
	 * <p>
	 * If {@code true}, the {@link WindDisturbance} will be removed from its {@code source}.
	 * <p>
	 * If {@code false}, the {@link WindDisturbance} will remain attached to its {@code source}.
	 *
	 * @param source The source of the {@link WindDisturbance}.
	 * @param level The current {@link Level}.
	 * @return Whether the {@link WindDisturbance} is expired, or can continue being used.
	 */
	public abstract boolean expired(T source, Level level);

	/**
	 * Determines whether a {@link WindDisturbance} can be attached to the given {@code source}.
	 *
	 * @param source The intended source of the {@link WindDisturbance}.
	 * @return Whether the {@link WindDisturbance} can be attached to the given {@code source}.
	 * @throws IllegalStateException If invalid (IDE only.)
	 */
	public final boolean checkValidSourceAndThrowIfInIDE(Object source) {
		if (this.isValidSource(source)) return true;

		if (ModLoader.isDevelopmentEnvironment()) {
			throw new IllegalStateException(
				"Invalid wind disturbance source for type %s, attempted to attach to %s"
					.formatted(FrozenLibBuiltInRegistries.WIND_DISTURBANCE_TYPE.getId(this.getType()), source)
			);
		}

		return false;
	}

	/**
	 * Determines whether a {@link WindDisturbance} can be attached to the given {@code source}.
	 *
	 * @param source The intended source of the {@link WindDisturbance}.
	 * @return Whether the {@link WindDisturbance} can be attached to the given {@code source}.
	 */
	protected abstract boolean isValidSource(Object source);

	/**
	 * This method enables {@link WindDisturbance}s to dictate the conditions under which
	 * multiple {@link WindDisturbance}s can be added to a single source.
	 * <p>
	 * If this returns {@code true}, the {@code other} {@link WindDisturbance} will not be added.
	 * <p>
	 * If this returns {@code false}, the {@code other} {@link WindDisturbance} will be added.
	 * <p>
	 * While this only compares each {@link WindDisturbance}'s type by default, certain
	 * types (such as those for individual non-{@link BlockEntity} {@link Block}s) may include
	 * custom constructor parameters and will want to use those for comparison
	 * instead (e.g., comparing the {@link BlockState} of both.)
	 *
	 * @param other The {@link WindDisturbance} to compare against.
	 * @return Whether {@code this} has the same properties as the {@code other} {@link WindDisturbance}.
	 */
	public boolean occupiesSameSlot(WindDisturbance<?> other) {
		return this.getType() == other.getType();
	}

	/**
	 * @return The {@link WindDisturbance}'s {@link WindDisturbanceType type}.
	 */
	public abstract WindDisturbanceType<?> getType();

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + this.getType() + "]";
	}

	public static final class Instance<S, T extends WindDisturbance<S>> {
		public static final Codec<Instance<?, ? extends WindDisturbance<?>>> CODEC = WindDisturbance.CODEC
			.xmap(Instance::new, instance -> instance.wrapped);
		public static final Codec<List<Instance<?, ? extends WindDisturbance<?>>>> LIST_CODEC = Codec.list(CODEC);
		public static final StreamCodec<RegistryFriendlyByteBuf, Instance<?, ? extends WindDisturbance<?>>> STREAM_CODEC = WindDisturbance.STREAM_CODEC
			.map(Instance::new, instance -> instance.wrapped);
		public static final StreamCodec<RegistryFriendlyByteBuf, List<Instance<?, ? extends WindDisturbance<?>>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

		private final T wrapped;
		private boolean initialized;
		private Vec3 origin;
		private double scale;
		private AABB area;

		private Instance(T wrapped) {
			this.wrapped = wrapped;
		}

		/**
		 * Creates a new {@link Instance}.
		 *
		 * @param disturbance The {@link WindDisturbance}.
		 * @return A new {@link Instance} for the given {@code disturbance}.
		 */
		public static <T extends WindDisturbance<?>> Instance<?, T> create(T disturbance) {
			return new Instance(disturbance);
		}

		/**
		 * Creates a new {@link Instance}.
		 *
		 * @param source The source of the {@link WindDisturbance}.
		 * @param disturbance The {@link WindDisturbance}.
		 * @param level The current {@link Level}.
		 * @return A new {@link Instance} for the given {@code source} and {@code disturbance}.
		 * @see Instance#create(WindDisturbance)
		 */
		public static <S, T extends WindDisturbance<S>> Instance<S, T> create(S source, T disturbance, Level level) {
			final Instance<S, T> instance = (Instance<S, T>) create(disturbance);
			instance.tick(source, level);
			return instance;
		}

		/**
		 * Updates the {@link WindDisturbance.Instance#origin origin position}, {@link WindDisturbance.Instance#scale scale}, and
		 * {@link WindDisturbance.Instance#area effective area} using values provided by the inner {@link WindDisturbance}.
		 *
		 * @param source The source of the inner {@link WindDisturbance}.
		 * @param level The current {@link Level}.
		 */
		public void tick(S source, Level level) {
			this.origin = this.wrapped.origin(source, level);
			this.scale = this.wrapped.scale(source, level);
			this.area = this.wrapped.area(source, level, this.origin, this.scale);
		}

		/**
		 * @return The current {@link WindDisturbance.Instance#area effective area}.
		 * @see WindDisturbance#area(Object, Level, Vec3, double)
		 */
		public AABB area() {
			return this.area;
		}

		/**
		 * @return Whether the given {@link Vec3 position} is within the {@link WindDisturbance.Instance#area effective area}.
		 */
		public boolean withinArea(Vec3 position) {
			return this.area.contains(position);
		}

		/**
		 * @return The current {@link WindDisturbance.Instance#origin origin position}.
		 * @see WindDisturbance#origin(Object, Level)
		 */
		public Vec3 origin() {
			return this.origin;
		}

		/**
		 * Computes the {@link WindDisturbanceResult result} of the inner {@link WindDisturbance}, either {@link WindDisturbanceResult#PASS passing} if
		 * it isn't effective, or returning its {@link WindDisturbanceResult#success(double, double, Vec3) strength, relative weight, and vector}
		 * if it is effective at the given {@link Vec3 target position}.
		 *
		 * @param source The source of the inner {@link WindDisturbance}.
		 * @param level The current {@link Level}.
		 * @param target The {@link Vec3 target position} to evaluate the inner {@link WindDisturbance}'s {@link WindDisturbanceResult result} at.
		 * @return The {@link WindDisturbanceResult result} of the inner {@link WindDisturbance}.
		 * @see WindDisturbance#sample(Object, Level, Vec3, AABB, Vec3, double)
		 */
		public WindDisturbanceResult sample(Level level, S source, Vec3 target) {
			if (!this.area.contains(target)) return WindDisturbanceResult.PASS;
			return this.wrapped.sample(source, level, this.origin, this.area, target, this.scale);
		}

		/**
		 * Determines whether the inner {@link WindDisturbance} has expired, or can continue being used.
		 * <p>
		 * If {@code true}, this will be removed from its {@code source}.
		 * <p>
		 * If {@code false}, this will remain attached to its {@code source}.
		 *
		 * @param source The source of the inner {@link WindDisturbance}.
		 * @param level The current {@link Level}.
		 * @return Whether the inner {@link WindDisturbance} has expired, or can continue being used.
		 * @see WindDisturbance#expired(Object, Level)
		 */
		public boolean expired(S source, Level level) {
			return this.wrapped.expired(source, level);
		}

		/**
		 * Determines whether the inner {@link WindDisturbance} can be attached to the given {@code source}.
		 *
		 * @param source The intended source of the inner {@link WindDisturbance}.
		 * @return Whether the inner {@link WindDisturbance} can be attached to the given {@code source}.
		 * @see WindDisturbance#isValidSource(Object)
		 */
		public boolean isValidSource(Object source) {
			return this.wrapped.isValidSource(source);
		}

		/**
		 * @param other The {@link WindDisturbance} to compare against.
		 * @return Whether {@code this} has the same properties as the {@code other} {@link WindDisturbance}.
		 * @see WindDisturbance#occupiesSameSlot(WindDisturbance)
		 */
		public boolean occupiesSameSlot(WindDisturbance<?> other) {
			return this.wrapped.occupiesSameSlot(other);
		}

		/**
		 * @param other The {@link Instance} to compare against.
		 * @return Whether {@code this} has the same properties as the {@code other} {@link Instance}.
		 * @see WindDisturbance#occupiesSameSlot(WindDisturbance)
		 */
		public boolean occupiesSameSlot(Instance<?, ?> other) {
			return this.occupiesSameSlot(other.wrapped);
		}

		/**
		 * @return The inner {@link WindDisturbance}'s {@link WindDisturbanceType type}.
		 * @see WindDisturbance#getType()
		 */
		public WindDisturbanceType<?> getType() {
			return this.wrapped.getType();
		}
	}
}
