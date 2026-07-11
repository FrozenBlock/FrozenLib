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

package net.frozenblock.lib.entity.api.category;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.ApiStatus;

public class MutableMobCategory {
	/**
	 * The id of the mod creating the {@link MobCategory}. Throws an exception if empty.
	 * <p>
	 * The new Enum's internal name will be {@code {modId}${name}} in uppercase.
	 */
	private final String modId;
	/**
	 * Only used when creating a new {@link MobCategory} and is called upon the new {@link MobCategory} being created.
	 * <p>
	 * This can be used to easily set a field's value to the newly created {@link MobCategory}, for example.
	 */
	private final Optional<Consumer<MobCategory>> creationCallback;
	/**
	 * The name of the {@link MobCategory}. Throws an exception if empty.
	 * <p>
	 * The new Enum's internal name will be {@code {modId}${name}} in uppercase.
	 */
	private String name;
	/**
	 * The abbreviation to use when this {@link MobCategory}'s information is displayed in the debug screen.
	 */
	private String debugAbbreviation;
	/**
	 * The maximum about of entities using this {@link MobCategory} that can exist around a player before spawning is disabled.
	 */
	private int max;
	/**
	 * Whether entities of this {@link MobCategory} are friendly.
	 * <p>
	 * If false, the values of {@link ServerLevel#isSpawningMonsters()} determines of natural spawning can occur and {@link ServerLevel#getDifficulty()} determines if Spawners can spawn these entities.
	 */
	private boolean isFriendly;
	/**
	 * Determines whether entities of this {@link MobCategory} can spawn after world generation.
	 * <p>
	 * You may notice that Animals cannot spawn after world generation, which is a result of this value being true.
	 */
	private boolean isPersistent;
	/**
	 * The default distance at which entities of this {@link MobCategory} will forcefully despawn, and where spawning no longer occurs.
	 * <p>
	 * Despawning can be overriden on a per-entity basis with {@link Mob#isPersistenceRequired()}, {@link Mob#requiresCustomPersistence()}, and {@link Mob#removeWhenFarAway(double)}.
	 */
	private int despawnDistance;

	private MutableMobCategory(String modId, Optional<Consumer<MobCategory>> creationCallback) {
		this.modId = modId;
		this.creationCallback = creationCallback;
	}

	/**
	 * Used alongside the Mob Category API to create a new {@link MobCategory}.
	 */
	public static MutableMobCategory create(
		String modId,
		String name,
		String debugAbbreviation,
		int max,
		boolean isFriendly,
		boolean isPersistent,
		int despawnDistance,
		Consumer<MobCategory> creationCallback
	) {
		if (StringUtil.isNullOrEmpty(modId)) throw new IllegalArgumentException("Custom Mob Category's modId cannot be empty!");
		if (StringUtil.isNullOrEmpty(name)) throw new IllegalArgumentException("Custom Mob Category's name cannot be empty!");
		if (StringUtil.isNullOrEmpty(debugAbbreviation)) throw new IllegalArgumentException("Custom Mob Category's debug abbreviation cannot be null!");

		final MutableMobCategory mutable = new MutableMobCategory(modId, Optional.of(creationCallback));
		mutable.name = name;
		mutable.debugAbbreviation = debugAbbreviation;
		mutable.max = max;
		mutable.isFriendly = isFriendly;
		mutable.isPersistent = isPersistent;
		mutable.despawnDistance = despawnDistance;
		return mutable;
	}

	@ApiStatus.Internal
	public String createNameWithModId(String buffer) {
		return this.modId + buffer + this.name;
	}

	@ApiStatus.Internal
	public void onCreated(MobCategory category) {
		this.creationCallback.orElseThrow().accept(category);
	}

	public String name() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String debugAbbreviation() {
		return this.debugAbbreviation;
	}

	public void setDebugAbbreviation(String debugAbbreviation) {
		this.debugAbbreviation = debugAbbreviation;
	}

	public int max() {
		return this.max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean isFriendly() {
		return this.isFriendly;
	}

	public void setIsFriendly(boolean isFriendly) {
		this.isFriendly = isFriendly;
	}

	public boolean isPersistent() {
		return this.isPersistent;
	}

	public void setIsPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}

	public int despawnDistance() {
		return this.despawnDistance;
	}

	public void setDespawnDistance(int despawnDistance) {
		this.despawnDistance = despawnDistance;
	}
}
