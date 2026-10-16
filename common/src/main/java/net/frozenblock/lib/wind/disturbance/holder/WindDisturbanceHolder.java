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

package net.frozenblock.lib.wind.disturbance.holder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.event.api.events.BlockEntityLifecycleEvents;
import net.frozenblock.lib.event.api.events.ChunkLifecycleEvents;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentTarget;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.BreezeWindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindChargeWindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class WindDisturbanceHolder<T> {
	@ApiStatus.Internal
	public static final Codec<WindDisturbanceHolder<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		WindDisturbance.Instance.LIST_CODEC.optionalFieldOf("wind_disturbances", List.of()).forGetter(holder -> holder.disturbances)
	).apply(instance, WindDisturbanceHolder::new));
	@ApiStatus.Internal
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbanceHolder<?>> STREAM_CODEC = StreamCodec.composite(
		WindDisturbance.Instance.LIST_STREAM_CODEC, holder -> holder.disturbances,
		WindDisturbanceHolder::new
	);
	@ApiStatus.Internal
	public static final DataAttachmentType<WindDisturbanceHolder<?>> ATTACHMENT_TYPE = DataAttachmentType.create(
		FrozenLibConstants.id("wind_disturbance_holder"),
		builder -> builder.persistent(CODEC).syncWith(STREAM_CODEC)
	);
	@ApiStatus.Internal
	private final List<WindDisturbance.Instance<?, ? extends WindDisturbance<?>>> disturbances = new ArrayList<>();
	@ApiStatus.Internal
	private @Nullable WindDisturbanceSourceType type;
	@ApiStatus.Internal
	private boolean initialized;

	private WindDisturbanceHolder() {}

	private WindDisturbanceHolder(List<WindDisturbance.Instance<?, ? extends WindDisturbance<?>>> disturbances) {
		this();
		this.disturbances.addAll(disturbances);
	}

	private WindDisturbanceHolder(
		List<WindDisturbance.Instance<?, ? extends WindDisturbance<?>>> disturbances,
		@Nullable WindDisturbanceSourceType type
	) {
		this(disturbances);
		this.type = type;
	}

	@ApiStatus.Internal
	public static void init() {
		EntityLifecycleEvents.ENTITY_LOAD.register((entity, level) -> {
			WindManager.getOrCreate(level).trackOrUntrackDisturbanceSource(entity);
			if (entity instanceof Breeze) tryAdd(level, entity, BreezeWindDisturbance.INSTANCE);
			if (entity instanceof WindCharge) tryAdd(level, entity, WindChargeWindDisturbance.INSTANCE);
		});
		EntityLifecycleEvents.ENTITY_UNLOAD.register((entity, level) -> WindManager.getOrCreate(level).untrackDisturbanceSource(entity));

		BlockEntityLifecycleEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).trackOrUntrackDisturbanceSource(blockEntity));
		BlockEntityLifecycleEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).untrackDisturbanceSource(blockEntity));

		ChunkLifecycleEvents.CHUNK_LOAD.register((serverLevel, chunk, generated) -> WindManager.getOrCreate(serverLevel).trackOrUntrackDisturbanceSource(chunk));
		ChunkLifecycleEvents.CHUNK_UNLOAD.register((serverLevel, chunk) -> WindManager.getOrCreate(serverLevel).untrackDisturbanceSource(chunk));
	}

	/**
	 * Attaches a {@link WindDisturbanceHolder} to the {@code source} if not {@link WindDisturbanceHolder#isEmpty() empty} or null,
	 * and {@link WindManager#trackDisturbanceSource(DataAttachmentTarget) tracks it}.
	 * <p>
	 * Removes a {@link WindDisturbanceHolder} from the {@code source} if {@link WindDisturbanceHolder#isEmpty() empty} or null,
	 * and {@link WindManager#untrackDisturbanceSource(DataAttachmentTarget)} untracks it}.
	 *
	 * @param source The source to attach/remove a {@link WindDisturbanceHolder} to/from.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void setOrRemove(Level level, DataAttachmentTarget source, @Nullable WindDisturbanceHolder disturbanceHolder) {
		if (disturbanceHolder == null || disturbanceHolder.isEmpty()) {
			ATTACHMENT_TYPE.remove(source);
			WindManager.getOrCreate(level).untrackDisturbanceSource(source);
		} else {
			disturbanceHolder.tick(source, level);
			ATTACHMENT_TYPE.set(source, disturbanceHolder);
			WindManager.getOrCreate(level).trackDisturbanceSource(source);
		}
	}

	/**
	 * Adds a {@link WindDisturbance} to the {@code source}'s {@link WindDisturbanceHolder}, and both
	 * {@link WindManager#trackDisturbanceSource(DataAttachmentTarget) tracks it} and re-sets its attachment for syncing.
	 *
	 * @param source The source of the {@link WindDisturbanceHolder} to add {@link WindDisturbance disturbance} to.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void tryAdd(Level level, DataAttachmentTarget source, WindDisturbance<?> disturbance) {
		final WindDisturbanceHolder holder = getOptional(source).orElseGet(() -> createForSource(source));
		if (!holder.canAdd(source, disturbance)) return;

		final WindDisturbanceHolder newHolder = holder.copyAndAdd(disturbance);
		setOrRemove(level, source, newHolder);
	}

	/**
	 * @param source The source to grab an attached {@link WindDisturbanceHolder} from.
	 * @return The attached {@link WindDisturbanceHolder}, if present.
	 * @throws IllegalStateException If the {@code source} is not a {@link DataAttachmentTarget}.
	 */
	@SuppressWarnings({"rawtypes"})
	public static Optional<WindDisturbanceHolder> getOptional(Object source) {
		if (!(source instanceof DataAttachmentTarget target)) throw new IllegalStateException(source + " is not a data attachment target!");

		return Optional.ofNullable(ATTACHMENT_TYPE.get(target));
	}

	/**
	 * Creates a {@link WindDisturbanceHolder} for the given {@code source}.
	 *
	 * @param source The source intended to be attached to.
	 * @return The newly-created {@link WindDisturbanceHolder}.
	 * @throws IllegalArgumentException If no {@link WindDisturbanceSourceType type} is found for the {@code source}.
	 * @see WindDisturbanceHolder#trySetSourceTypeIfNotPresent(Object)
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static WindDisturbanceHolder createForSource(DataAttachmentTarget source) {
		final WindDisturbanceHolder holder = new WindDisturbanceHolder();
		holder.trySetSourceTypeIfNotPresent(source);
		return holder;
	}

	/**
	 * Attempts to update the {@link WindDisturbanceHolder}'s {@link WindDisturbanceHolder#type} if it has not yet been set.
	 * @param source The source intended to be attached to.
	 * @throws IllegalArgumentException If no {@link WindDisturbanceSourceType type} is found for the {@code source}
	 * or an existing {@link WindDisturbance.Instance} does not support the {@code source}.
	 */
	private void trySetSourceTypeIfNotPresent(@Nullable Object source) {
		if (this.type != null) return;
		if (source == null) return;

		final WindDisturbanceSourceType type = WindDisturbanceSourceType.typeFor(source);
		if (type == null) throw new IllegalArgumentException("Unknown type for source: " + source);

		this.disturbances.forEach(disturbance -> {
			if (!disturbance.isValidSource(source)) {
				throw new IllegalStateException("Attempting to set source to " + source + ", but " + disturbance.getType() + " does not support it!");
			}
		});

		this.type = type;
	}

	/**
	 * @param source The source this is attached to.
	 * @return Whether the {@code source} is still valid to act as a {@link WindDisturbanceHolder}.
	 */
	public boolean isValidSource(@Nullable Object source) {
		this.trySetSourceTypeIfNotPresent(source);
		return this.type.validate(source);
	}

	/**
	 * Ticks each held {@link WindDisturbance.Instance}, and removes those that have expired.
	 *
	 * @param source The source this is attached to.
	 * @param level The current {@link Level}.
	 */
	@SuppressWarnings("unchecked")
	public void tick(T source, Level level) {
		this.initializeIfNot(source, level);
		if (this.type == null) throw new IllegalStateException("Wind disturbance holder is not yet attached to a source!");

		((List<WindDisturbance.Instance>) (Object) this.disturbances).removeIf(disturbance -> {
			if (disturbance.expired(source, level)) return true;

			disturbance.tick(source, level);
			return false;
		});
	}

	/**
	 * Invokes the necessary methods required to set up a {@link WindDisturbanceHolder}.
	 *
	 * @param source The source this is attached to.
	 * @param level The current {@link Level}.
	 * @see WindDisturbanceHolder#trySetSourceTypeIfNotPresent(Object)
	 */
	public void initializeIfNot(T source, Level level) {
		if (this.initialized) return;

		this.trySetSourceTypeIfNotPresent(source);
		this.initialized = true;
	}

	/**
	 * @return All held {@link WindDisturbance.Instance}s.
	 */
	public List<WindDisturbance.Instance<?, ? extends WindDisturbance<?>>> disturbances() {
		if (this.type == null) throw new IllegalStateException("Wind disturbance holder is not yet attached to a source!");
		return this.disturbances;
	}

	/**
	 * Determines whether a new {@link WindDisturbance} can be added to the holder.
	 * <p>
	 * If {@link WindDisturbance#occupiesSameSlot(WindDisturbance)} returns {@code true} among any of the held {@link WindDisturbance.Instance}s,
	 * the {@code disturbance} will not be added.
	 *
	 * @param disturbance The {@link WindDisturbance} to add.
	 * @return Whether the {@code disturbance} can be added.
	 * @throws IllegalArgumentException If the {@code disturbance}'s {@link WindDisturbanceType type} does not support the source's {@link WindDisturbanceSourceType type}.
	 */
	private boolean canAdd(Object source, WindDisturbance<?> disturbance) {
		if (disturbance.getType().sourceType() != this.type) {
			throw new IllegalArgumentException("Wind disturbance " + disturbance + " cannot be used with type: " + this.type);
		}

		if (!disturbance.checkValidSourceAndThrowIfInIDE(source)) return false;

		return this.disturbances.stream().noneMatch(other -> other.occupiesSameSlot(disturbance));
	}

	/**
	 * @param disturbance The {@link WindDisturbance} to add.
	 * @return A copy of {@code this} with the {@code disturbance} added on.
	 */
	private WindDisturbanceHolder<T> copyAndAdd(WindDisturbance<T> disturbance) {
		final List<WindDisturbance.Instance<?, ? extends WindDisturbance<?>>> disturbances = new ArrayList<>(this.disturbances);
		disturbances.add(WindDisturbance.Instance.create(disturbance));

		return new WindDisturbanceHolder<>(disturbances, this.type);
	}

	/**
	 * @return Whether this is empty. This is self-explanatory. Why are you reading the Javadoc for it. Why. {@code WHY.}
	 */
	public boolean isEmpty() {
		return this.disturbances.isEmpty();
	}
}
