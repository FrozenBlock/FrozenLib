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

package net.frozenblock.lib.levelgen.feature.api;

import java.util.Set;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;

@UtilityClass
public final class FrozenLibFeatureUtil {
	public static BootstrapContext<Object> BOOTSTRAP_CONTEXT = null;

	public static boolean isBlockExposed(LevelAccessor level, BlockPos pos) {
		final BlockPos.MutableBlockPos mutable = pos.mutable();
		for (Direction direction : Direction.values()) {
			final BlockState state = level.getBlockState(mutable.setWithOffset(pos, direction));
			if (state.canBeReplaced()) return true;
		}
		return false;
	}

	public static boolean matchesConditionsTouching(LevelAccessor level, BlockPos pos, boolean requiredOnAllSides, BlockPredicate predicate) {
		final BlockPos.MutableBlockPos mutable = pos.mutable();

		int validSides = 0;
		for (Direction direction : Direction.values()) {
			if (!predicate.test(level, mutable.setWithOffset(pos, direction))) continue;
			if (!requiredOnAllSides) return true;
			validSides += 1;
		}

		return validSides == 6;
	}

	public static boolean isAirOrWaterNearby(LevelAccessor level, BlockPos pos, int searchDistance) {
		return matchesConditionNearby(level, pos, searchDistance, BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE);
	}

	public static boolean isWaterNearby(LevelAccessor level, BlockPos pos, int searchDistance) {
		return matchesConditionNearby(level, pos, searchDistance, BlockPredicate.matchesBlocks(Blocks.WATER));
	}

	public static boolean matchesConditionNearby(LevelAccessor level, BlockPos pos, int searchDistance, BlockPredicate predicate) {
		final Iterable<BlockPos> poses = BlockPos.betweenClosed(
			pos.offset(-searchDistance, -searchDistance, -searchDistance),
			pos.offset(searchDistance, searchDistance, searchDistance)
		);
		for (BlockPos currentPos : poses) if (predicate.test(level, currentPos)) return true;
		return false;
	}

	public static ResourceKey<Feature> createKey(String namespace, String path) {
		return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(namespace, path));
	}

	public static <F extends Feature> Holder.Reference<F> register(
		DynamicRegistryManagerSetupContext context,
		DynamicRegistryManagerSetupContext.RegistryMap registries,
		String namespace,
		String id,
		F feature
	) {
		Registry.register(registries.get(Registries.FEATURE), Identifier.fromNamespaceAndPath(namespace, id), feature);
		return (Holder.Reference<F>) getExact(registries, feature);
	}

	public static void register(BootstrapContext<Feature> context, ResourceKey<Feature> key, Feature feature) {
		register(context, key, feature);
	}

	public static Holder<Feature> register(DynamicRegistryManagerSetupContext entries, ResourceKey<Feature> key, Feature feature) {
		final DynamicRegistryManagerSetupContext.RegistryMap registries = entries.getRegistries(Set.of(Registries.FEATURE));
		final Feature value = registries.register(Registries.FEATURE, key.identifier(), feature);
		return Holder.direct(value);
	}

	public static Holder.Reference<Feature> getExact(DynamicRegistryManagerSetupContext.RegistryMap registries, Feature feature) {
		final Registry<Feature> configuredRegistry = registries.get(Registries.FEATURE);
		return configuredRegistry.getOrThrow(configuredRegistry.getResourceKey(feature).orElseThrow());
	}

	public static Holder<Feature> getHolder(ResourceKey<Feature> resourceKey) {
		return VanillaRegistries.createWorldLookup().lookupOrThrow(Registries.FEATURE).getOrThrow(resourceKey);
	}
}
