/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.registry.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.entity.api.rendering.EntityTextureOverride;
import net.frozenblock.lib.integration.api.ModIntegrationSupplier;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.api.SpottingIconPredicate;
import net.minecraft.core.MappedRegistry;

public class FrozenRegistry {

	public static final MappedRegistry<ModIntegrationSupplier> MOD_INTEGRATION = FabricRegistryBuilder.createSimple(ModIntegrationSupplier.class, FrozenMain.id("mod_integration"))
			.buildAndRegister();

	public static final MappedRegistry<SoundPredicate> SOUND_PREDICATE = FabricRegistryBuilder.createSimple(SoundPredicate.class, FrozenMain.id("sound_predicate_synced"))
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final MappedRegistry<SoundPredicate> SOUND_PREDICATE_UNSYNCED = FabricRegistryBuilder.createSimple(SoundPredicate.class, FrozenMain.id("sound_predicate"))
			.buildAndRegister();

	public static final MappedRegistry<SpottingIconPredicate> SPOTTING_ICON_PREDICATE = FabricRegistryBuilder.createSimple(SpottingIconPredicate.class, FrozenMain.id("spotting_icon_predicate_synced"))
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

    public static void initRegistry() {

    }
}
