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

package net.frozenblock.lib.entity.api.cubemob.sulfurcube;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public final class SulfurCubeEvents {
	public static final DataAttachmentType<Boolean> POWERED = DataAttachmentType.create(
		FrozenLibConstants.id("sulfur_cube_powered"),
		builder -> {
			builder.initializer(() -> false);
			builder.persistent(Codec.BOOL);
		}
	);

	/**
	 * The event that is triggered when a {@link Player} interacts with a {@link SulfurCube}.
	 */
	public static final Event<Interact> ON_INTERACT = EventRegistry.createEnvironmentEvent(
		Interact.class,
		(callbacks) -> (sulfurCube, player, hand) -> {
			for (var callback : callbacks) {
				final Optional<InteractionResult> interactionResult = callback.onInteract(sulfurCube, player, hand);
				if (interactionResult.isPresent()) return interactionResult;
			}
			return Optional.empty();
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} clears its {@link SulfurCubeArchetype}-related data each tick.
	 */
	public static final Event<ArchetypeDataRemove> ON_ARCHETYPE_DATA_REMOVE = EventRegistry.createEnvironmentEvent(
		ArchetypeDataRemove.class,
		(callbacks) -> (sulfurCube, archetypeDataStorage) -> {
			for (var callback : callbacks) callback.onArchetypeDataRemove(sulfurCube, archetypeDataStorage);
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} applies its {@link SulfurCubeArchetype} each tick.
	 */
	public static final Event<ArchetypeApply> ON_ARCHETYPE_APPLY = EventRegistry.createEnvironmentEvent(
		ArchetypeApply.class,
		(callbacks) -> (sulfurCube, archetype, archetypeDataStorage) -> {
			for (var callback : callbacks) callback.onArchetypeApply(sulfurCube, archetype, archetypeDataStorage);
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} becomes powered or unpowered via redstone.
	 */
	public static final Event<Power> ON_POWER_CHANGED = EventRegistry.createEnvironmentEvent(
		Power.class,
		(callbacks) -> (sulfurCube, powered) -> {
			for (var callback : callbacks) callback.onPowerChanged(sulfurCube, powered);
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} plays its push sound.
	 */
	public static final Event<PushSoundPlayed> ON_PUSH_SOUND_PLAYED = EventRegistry.createEnvironmentEvent(
		PushSoundPlayed.class,
		(callbacks) -> (sulfurCube, player, pushVelocity) -> {
			for (var callback : callbacks) callback.onPushSoundPlayed(sulfurCube, player, pushVelocity);
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} is pushed.
	 */
	public static final Event<Push> ON_PUSH = EventRegistry.createEnvironmentEvent(
		Push.class,
		(callbacks) -> (sulfurCube, player, pushVelocity) -> {
			for (var callback : callbacks) callback.onPush(sulfurCube, player, pushVelocity);
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} is hit.
	 */
	public static final Event<Hit> ON_HIT = EventRegistry.createEnvironmentEvent(
		Hit.class,
		(callbacks) -> (sulfurCube, pushVelocity, source, damage, comesFromEffect) -> {
			for (var callback : callbacks) callback.onHit(sulfurCube, pushVelocity, source, damage, comesFromEffect);
		}
	);

	/**
	 * The event that is triggered when a {@link SulfurCube} squishes (lands on the ground.)
	 */
	public static final Event<Squish> ON_SQUISH = EventRegistry.createEnvironmentEvent(
		Squish.class,
		(callbacks) -> (sulfurCube) -> {
			for (var callback : callbacks) callback.onSquish(sulfurCube);
		}
	);

	public static void init() {}

	/**
	 * A functional interface representing a Sulfur Cube interact event.
	 */
	@FunctionalInterface
	public interface Interact extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} squishes (lands on the ground.)
		 * @param sulfurCube the {@link SulfurCube} being interacted with
		 * @param player the {@link Player} interacting with the {@link SulfurCube}
		 * @param hand the {@link InteractionHand} used in the interaction
		 */
		Optional<InteractionResult> onInteract(SulfurCube sulfurCube, Player player, InteractionHand hand);
	}

	/**
	 * A functional interface representing a Sulfur Cube archetype data remove event.
	 */
	@FunctionalInterface
	public interface ArchetypeDataRemove extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} clears its {@link SulfurCubeArchetype}-related data each tick.
		 * @param sulfurCube the {@link SulfurCube} clearing its {@link SulfurCubeArchetype}-related data
		 * @param archetypeDataStorage the {@link ArchetypeDataStorage}, used to store custom data between this event and the archetype apply event
		 */
		void onArchetypeDataRemove(SulfurCube sulfurCube, ArchetypeDataStorage archetypeDataStorage);
	}

	/**
	 * A functional interface representing a Sulfur Cube archetype apply event.
	 */
	@FunctionalInterface
	public interface ArchetypeApply extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} applies its {@link SulfurCubeArchetype} each tick.
		 * @param sulfurCube the {@link SulfurCube} applying its {@link SulfurCubeArchetype}
		 * @param archetype the {@link SulfurCubeArchetype} being applied, in {@link Holder} form
		 * @param archetypeDataStorage the {@link ArchetypeDataStorage} from the archetype data remove event
		 */
		void onArchetypeApply(SulfurCube sulfurCube, Holder<SulfurCubeArchetype> archetype, ArchetypeDataStorage archetypeDataStorage);
	}

	/**
	 * A functional interface representing a Sulfur Cube power event.
	 */
	@FunctionalInterface
	public interface Power extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} becomes powered or unpowered via redstone.
		 * @param sulfurCube the {@link SulfurCube} becoming powered or unpowered
		 * @param powered whether the {@link SulfurCube} is becoming powered or unpowered
		 */
		void onPowerChanged(SulfurCube sulfurCube, boolean powered);
	}

	/**
	 * A functional interface representing a Sulfur Cube push sound played event.
	 */
	@FunctionalInterface
	public interface PushSoundPlayed extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} plays its push sound.
		 * @param sulfurCube the {@link SulfurCube} playing the push sound
		 * @param player the pushing {@link Player}
		 * @param pushVelocity the velocity of the push in {@link Vec3} form
		 */
		void onPushSoundPlayed(SulfurCube sulfurCube, Player player, Vec3 pushVelocity);
	}

	/**
	 * A functional interface representing a Sulfur Cube push event.
	 */
	@FunctionalInterface
	public interface Push extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} is pushed.
		 * @param sulfurCube the pushed {@link SulfurCube}
		 * @param player the pushing {@link Player}
		 * @param pushVelocity the velocity of the push in {@link Vec3} form
		 */
		void onPush(SulfurCube sulfurCube, Player player, Vec3 pushVelocity);
	}

	/**
	 * A functional interface representing a Sulfur Cube hit event.
	 */
	@FunctionalInterface
	public interface Hit extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} is hit.
		 * @param sulfurCube the hit {@link SulfurCube}
		 * @param hitVelocity the velocity of the hit in {@link Vec3} form
		 * @param source the {@link DamageSource} of the damage
		 * @param damage the amount of damage
		 * @param comesFromEffect whether the knockback was caused by something other than a direct hit (such as knockback enchantments and spears)
		 */
		void onHit(SulfurCube sulfurCube, Vec3 hitVelocity, DamageSource source, float damage, boolean comesFromEffect);
	}

	/**
	 * A functional interface representing a Sulfur Cube squish event.
	 */
	@FunctionalInterface
	public interface Squish extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link SulfurCube} squishes (lands on the ground.)
		 * @param sulfurCube the squishing {@link SulfurCube}
		 */
		void onSquish(SulfurCube sulfurCube);
	}

	public static final class ArchetypeStorageDataKey<T> {

		private ArchetypeStorageDataKey() {}

		public static <T> ArchetypeStorageDataKey<T> create() {
			return new ArchetypeStorageDataKey<>();
		}
	}

	public static final class ArchetypeDataStorage {
		private Map<ArchetypeStorageDataKey<?>, Object> sulfurCubeArchetypeData;

		@ApiStatus.Internal
		public ArchetypeDataStorage() {}

		@SuppressWarnings("unchecked")
		@Nullable
		public <T> T getData(ArchetypeStorageDataKey<T> key) {
			return sulfurCubeArchetypeData == null
				? null
				: (T) sulfurCubeArchetypeData.get(key);
		}

		@SuppressWarnings("unchecked")
		public <T> T getDataOrDefault(ArchetypeStorageDataKey<T> key, T defaultValue) {
			return sulfurCubeArchetypeData == null
				? defaultValue
				: (T) sulfurCubeArchetypeData.getOrDefault(key, defaultValue);
		}

		public <T> void setData(ArchetypeStorageDataKey<T> key, T value) {
			if (sulfurCubeArchetypeData == null) sulfurCubeArchetypeData = new Reference2ObjectOpenHashMap<>();
			sulfurCubeArchetypeData.put(key, value);
		}
	}
}
