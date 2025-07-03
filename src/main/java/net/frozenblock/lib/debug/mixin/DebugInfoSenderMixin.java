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

package net.frozenblock.lib.debug.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.debug.networking.ImprovedGameEventDebugPayload;
import net.frozenblock.lib.debug.networking.ImprovedGameEventListenerDebugPayload;
import net.frozenblock.lib.debug.networking.ImprovedGoalDebugPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.network.protocol.common.custom.NeighborUpdatesDebugPayload;
import net.minecraft.network.protocol.common.custom.PathfindingDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiAddedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiRemovedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiTicketCountDebugPayload;
import net.minecraft.network.protocol.common.custom.RaidsDebugPayload;
import net.minecraft.network.protocol.common.custom.VillageSectionsDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugPackets.class)
public class DebugInfoSenderMixin {

	@Inject(method = "sendPoiAddedPacket", at = @At("HEAD"))
	private static void frozenLib$sendPoiAddition(ServerLevel level, BlockPos pos, CallbackInfo info) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		level.getPoiManager().getType(pos).ifPresent((registryEntry) -> {
			sendPacketToAllPlayers(
				level,
				new PoiAddedDebugPayload(
					pos,
					registryEntry.getRegisteredName(),
					level.getPoiManager().getFreeTickets(pos)
				)
			);
		});
	}

	@Inject(method = "sendPoiRemovedPacket", at = @At("HEAD"))
	private static void frozenLib$sendPoiRemoval(ServerLevel serverLevel, BlockPos pos, CallbackInfo info) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		sendPacketToAllPlayers(serverLevel, new PoiRemovedDebugPayload(pos));
	}

	@Inject(method = "sendPoiTicketCountPacket", at = @At("HEAD"))
	private static void frozenLib$sendPointOfInterest(ServerLevel serverLevel, BlockPos pos, CallbackInfo info) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		sendPacketToAllPlayers(serverLevel, new PoiTicketCountDebugPayload(pos, serverLevel.getPoiManager().getFreeTickets(pos)));
	}

	@Inject(method = "sendVillageSectionsPacket", at = @At("HEAD"))
	private static void frozenLib$sendPoi(ServerLevel serverLevel, BlockPos pos, CallbackInfo info) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		Registry<Structure> registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
		SectionPos chunkSectionPos = SectionPos.of(pos);
		Iterator<Holder<Structure>> villageIterator = registry.getTagOrEmpty(StructureTags.VILLAGE).iterator();

		Holder<Structure> structureHolder;

		do {
			if (!villageIterator.hasNext()) {
				sendPacketToAllPlayers(serverLevel, new VillageSectionsDebugPayload(Set.of(), Set.of(chunkSectionPos)));
				return;
			}
			structureHolder = villageIterator.next();
		} while (serverLevel.structureManager().startsForStructure(chunkSectionPos, structureHolder.value()).isEmpty());

		sendPacketToAllPlayers(serverLevel, new VillageSectionsDebugPayload(Set.of(chunkSectionPos), Set.of()));
	}

	@Inject(method = "sendPathFindingPacket", at = @At("HEAD"))
	private static void frozenLib$sendPathfindingData(Level level, Mob mob, @Nullable Path path, float nodeReachProximity, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && path != null && level instanceof ServerLevel serverLevel) {
			sendPacketToAllPlayers(serverLevel, new PathfindingDebugPayload(mob.getId(), path, nodeReachProximity));
		}
	}

	@Inject(method = "sendNeighborsUpdatePacket", at = @At("HEAD"))
	private static void frozenLib$sendNeighborUpdate(Level level, BlockPos pos, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && level instanceof ServerLevel serverLevel) {
			sendPacketToAllPlayers(serverLevel, new NeighborUpdatesDebugPayload(serverLevel.getGameTime(), pos));
		}
	}

	@Inject(method = "sendGoalSelector", at = @At("HEAD"))
	private static void frozenLib$sendGoalSelector(Level level, Mob mob, GoalSelector goalSelector, CallbackInfo info) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		List<GoalDebugPayload.DebugGoal> goals = mob.goalSelector.getAvailableGoals().stream().map(
			(goal) -> new GoalDebugPayload.DebugGoal(goal.getPriority(), goal.isRunning(), goal.getGoal().toString())
		).toList();
		sendPacketToAllPlayers((ServerLevel) level, new ImprovedGoalDebugPayload(mob.getId(), goals));
	}

	@Inject(method = "sendRaids", at = @At("HEAD"))
	private static void frozenLib$sendRaids(ServerLevel level, Collection<Raid> raids, CallbackInfo info) {
		if (!FrozenLibConfig.IS_DEBUG) return;
		sendPacketToAllPlayers(level, new RaidsDebugPayload(raids.stream().map(Raid::getCenter).toList()));
	}

	@Inject(method = "sendEntityBrain", at = @At("HEAD"))
	private static void frozenLib$sendBrainDebugData(LivingEntity livingEntity, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && livingEntity instanceof Mob mob && mob.level() instanceof ServerLevel serverLevel) {
			int angerLevel = -1;
			if (mob instanceof Warden wardenEntity) {
				angerLevel = wardenEntity.getClientAngerLevel();
			}

			List<String> gossipList = new ArrayList<>();
			Set<BlockPos> pois = new HashSet<>();
			Set<BlockPos> potentialPois = new HashSet<>();
			String profession = "";
			int xp = 0;
			String inventory = "";
			boolean wantsGolem = false;
			if (mob instanceof Villager villager) {
				profession = villager.getVillagerData().profession().toString();
				xp = villager.getVillagerXp();
				inventory = villager.getInventory().toString();
				wantsGolem = villager.wantsToSpawnGolem(serverLevel.getGameTime());
				villager.getGossips().getGossipEntries().forEach((uuid, associatedGossip) -> {
					Entity gossipEntity = serverLevel.getEntity(uuid);
					if (gossipEntity != null) {
						String name = DebugEntityNameGenerator.getEntityName(gossipEntity);

						for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<GossipType> gossipTypeEntry : associatedGossip.object2IntEntrySet()) {
							gossipList.add(name + ": " + (gossipTypeEntry.getKey()).getSerializedName() + " " + gossipTypeEntry.getValue());
						}
					}

				});
				Brain<?> brain = villager.getBrain();
				frozenLib$addPoi(brain, MemoryModuleType.HOME, pois);
				frozenLib$addPoi(brain, MemoryModuleType.JOB_SITE, pois);
				frozenLib$addPoi(brain, MemoryModuleType.MEETING_POINT, pois);
				frozenLib$addPoi(brain, MemoryModuleType.HIDING_PLACE, pois);
				frozenLib$addPoi(brain, MemoryModuleType.POTENTIAL_JOB_SITE, potentialPois);
			}

			sendPacketToAllPlayers(
				serverLevel,
				new BrainDebugPayload(
					new BrainDebugPayload.BrainDump(
						mob.getUUID(),
						mob.getId(),
						mob.getName().getString(),
						profession,
						xp,
						mob.getHealth(),
						mob.getMaxHealth(),
						mob.position(),
						inventory,
						mob.getNavigation().getPath(),
						wantsGolem,
						angerLevel,
						mob.getBrain().getActiveActivities().stream().map(Activity::toString).toList(),
						mob.getBrain().getRunningBehaviors().stream().map(BehaviorControl::debugString).toList(),
						getMemoryDescriptions(mob, serverLevel.getGameTime()),
						gossipList,
						pois,
						potentialPois
					)
				));
		}
	}

	@Unique
	private static void frozenLib$addPoi(Brain<?> brain, MemoryModuleType<GlobalPos> memoryModuleType, Set<BlockPos> set) {
		if (set != null) brain.getMemory(memoryModuleType).map(GlobalPos::pos).ifPresent(set::add);
	}

	@Inject(method = "sendBeeInfo", at = @At("HEAD"))
	private static void frozenLib$sendBeeDebugData(Bee bee, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && bee.level() instanceof ServerLevel serverLevel) {
			sendPacketToAllPlayers(
				serverLevel,
				new BeeDebugPayload(
					new BeeDebugPayload.BeeInfo(
						bee.getUUID(),
						bee.getId(),
						bee.position(),
						bee.getNavigation().getPath(),
						bee.getHivePos(),
						bee.getSavedFlowerPos(),
						bee.getTravellingTicks(),
						bee.getGoalSelector().getAvailableGoals().stream().map((prioritizedGoal) -> prioritizedGoal.getGoal().toString()).collect(Collectors.toSet()),
						bee.getBlacklistedHives()
					)
				)
			);
		}
	}

	@Inject(method = "sendBreezeInfo", at = @At("HEAD"))
	private static void frozenLib$sendBreezeDebugData(Breeze breeze, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && breeze.level() instanceof ServerLevel serverLevel) {
			sendPacketToAllPlayers(
				serverLevel,
				new BreezeDebugPayload(
					new BreezeDebugPayload.BreezeInfo(
						breeze.getUUID(),
						breeze.getId(),
						breeze.getTarget() == null ? null : breeze.getTarget().getId(),
						breeze.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_TARGET).orElse(null)
					)
				)
			);
		}
	}

	@Inject(method = "sendGameEventInfo", at = @At("HEAD"))
	private static void frozenLib$sendGameEvent(Level level, Holder<GameEvent> event, Vec3 pos, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && level instanceof ServerLevel serverLevel) {
			event.unwrapKey().ifPresent((key) -> sendPacketToAllPlayers(serverLevel, new ImprovedGameEventDebugPayload(key, pos)));
		}
	}

	@Inject(method = "sendGameEventListenerInfo", at = @At("HEAD"))
	private static void frozenLib$sendGameEventListener(Level level, GameEventListener eventListener, CallbackInfo info) {
		if (FrozenLibConfig.IS_DEBUG && level instanceof ServerLevel serverLevel) {
			sendPacketToAllPlayers(
				serverLevel,
				new ImprovedGameEventListenerDebugPayload(
					eventListener.getListenerSource(),
					eventListener.getListenerRadius()
				)
			);
		}
	}

	@Shadow
	private static void sendPacketToAllPlayers(ServerLevel level, CustomPacketPayload payload) {
		throw new AssertionError("Mixin injection failed - FrozenLib DebugInfoSenderMixin.");
	}

	@Shadow
	private static List<String> getMemoryDescriptions(LivingEntity entity, long currentTime) {
		throw new AssertionError("Mixin injection failed - FrozenLib DebugInfoSenderMixin.");
	}
}
