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

package net.frozenblock.lib.entrypoint.api;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

/**
 * An edited version of {@link ModInitializer} that contains extra tools for ease of use.
 */
public abstract class FrozenModInitializer implements ModInitializer {
	private final String modId;
	private final ModContainer container;

	protected FrozenModInitializer(String modId) {
		this.modId = modId;
		this.container = FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalStateException("Mod container not found for mod id " + modId));
	}

	@Override
	public void onInitialize() {
		this.onInitialize(this.modId, this.container);
	}

	public abstract void onInitialize(String modId, ModContainer container);

	/**
	 * @return this mod's id.
	 */
	public String modId() {
		return this.modId;
	}

	/**
	 * @return the {@link ModContainer} of the mod being initialized.
	 */
	public ModContainer container() {
		return this.container;
	}

	/**
	 * @return a {@link Identifier} with this mod's id as the namespace.
	 */
	public Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(this.modId, path);
	}

	/**
	 * Registers an object using this mod's id as the namespace.
	 *
	 * @return the registered value.
	 */
	public <T> T register(Registry<T> registry, String path, T value) {
		return Registry.register(registry, this.id(path), value);
	}
}
