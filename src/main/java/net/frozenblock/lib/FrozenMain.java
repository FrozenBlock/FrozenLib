/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.ConfigCommand;
import net.frozenblock.lib.core.impl.DataPackReloadMarker;
import net.frozenblock.lib.entity.api.command.ScaleEntityCommand;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.event.api.PlayerJoinEvents;
import net.frozenblock.lib.event.api.RegistryFreezeEvents;
import net.frozenblock.lib.ingamedevtools.RegisterInGameDevTools;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.particle.api.FrozenParticleTypes;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.frozenblock.lib.screenshake.api.command.ScreenShakeCommand;
import net.frozenblock.lib.screenshake.impl.ScreenShakeStorage;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.api.SpottingIconPredicate;
import net.frozenblock.lib.tag.api.TagKeyArgument;
import net.frozenblock.lib.tag.api.TagListCommand;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.api.command.WindOverrideCommand;
import net.frozenblock.lib.wind.impl.WindStorage;
import net.frozenblock.lib.worldgen.feature.api.FrozenFeatures;
import net.frozenblock.lib.worldgen.feature.api.placementmodifier.FrozenPlacementModifiers;
import net.frozenblock.lib.worldgen.surface.impl.BiomeTagConditionSource;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.WardenSpawnTrackerCommand;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;

public final class FrozenMain implements ModInitializer {

	@Override
	public void onInitialize() {
		FrozenRegistry.initRegistry();

		// QUILT INIT

		ServerFreezer.onInitialize();
		ModProtocol.loadVersions();
		ServerRegistrySync.registerHandlers();

		// CONTINUE FROZENLIB INIT

		SoundPredicate.init();
		SpottingIconPredicate.init();
		FrozenFeatures.init();
		FrozenPlacementModifiers.init();
		DataPackReloadMarker.init();

		Registry.register(BuiltInRegistries.MATERIAL_CONDITION, FrozenSharedConstants.id("biome_tag_condition_source"), BiomeTagConditionSource.CODEC.codec());

		RegisterInGameDevTools.register();
		FrozenParticleTypes.registerParticles();

		FrozenMainEntrypoint.EVENT.invoker().init(); // includes dev init

		ArgumentTypeInfos.register(
			BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
			FrozenSharedConstants.string("tag_key"),
			ArgumentTypeInfos.fixClassType(TagKeyArgument.class),
			new TagKeyArgument.Info<>()
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			WindOverrideCommand.register(dispatcher);
			ScreenShakeCommand.register(dispatcher);
			ConfigCommand.register(dispatcher);
			TagListCommand.register(dispatcher);
			ScaleEntityCommand.register(dispatcher);
		});

		ServerWorldEvents.LOAD.register((server, level) -> {
			DimensionDataStorage dimensionDataStorage = level.getDataStorage();
			WindManager windManager = WindManager.getWindManager(level);
			dimensionDataStorage.computeIfAbsent(windManager.createData(), WindStorage.WIND_FILE_ID);
			ScreenShakeManager screenShakeManager = ScreenShakeManager.getScreenShakeManager(level);
			dimensionDataStorage.computeIfAbsent(screenShakeManager.createData(), ScreenShakeStorage.SCREEN_SHAKE_FILE_ID);
		});

		ServerTickEvents.START_WORLD_TICK.register(serverLevel -> {
			WindManager.getWindManager(serverLevel).tick();
			ScreenShakeManager.getScreenShakeManager(serverLevel).tick();
		});

		PlayerJoinEvents.ON_PLAYER_ADDED_TO_LEVEL.register(((server, serverLevel, player) -> {
			WindManager windManager = WindManager.getWindManager(serverLevel);
			windManager.sendSyncToPlayer(windManager.createSyncByteBuf(), player);
		}));

		if (FrozenLibConfig.get().wardenSpawnTrackerCommand)
			CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> WardenSpawnTrackerCommand.register(dispatcher)));

		FrozenNetworking.registerNetworking();

		RegistryFreezeEvents.START_REGISTRY_FREEZE.register((registry, allRegistries) -> {
			if (!allRegistries) return;
			ModIntegrations.initialize();
		});
	}

	@Deprecated(forRemoval = true)
	public static ResourceLocation id(String path) {
		return new ResourceLocation(FrozenSharedConstants.MOD_ID, path);
	}

	@Deprecated(forRemoval = true)
	public static String string(String path) {
		return id(path).toString();
	}

}
