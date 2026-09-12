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

package net.frozenblock.lib.config.v2.entry.predicates;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public final class FrozenLibConfigPredicates {
	public static final ResourceKey<ConfigPredicate> FALSE = create("false");
	public static final ResourceKey<ConfigPredicate> HAS_WILDER_WILD = create("has_wilder_wild");
	public static final ResourceKey<ConfigPredicate> HAS_TRAILIER_TALES = create("has_trailier_tales");
	public static final ResourceKey<ConfigPredicate> HAS_THE_COPPERIER_AGE = create("has_the_copperier_age");
	public static final ResourceKey<ConfigPredicate> HAS_CHAOS_HYPERCUBED = create("has_chaos_hypercubed");
	public static final ResourceKey<ConfigPredicate> HAS_NETHERIER_NETHER = create("has_netherier_nether");
	public static final ResourceKey<ConfigPredicate> HAS_SPRINGIER_LIFE = create("has_springier_life");
	public static final ResourceKey<ConfigPredicate> HAS_SIMPLE_COPPER_PIPES = create("has_simple_copper_pipes");
	public static final ResourceKey<ConfigPredicate> HAS_FREEZE_FRAME = create("has_freeze_frame");
	public static final ResourceKey<ConfigPredicate> HAS_GLOWTONE = create("has_glowtone");

	public static void bootstrap(BootstrapContext<ConfigPredicate> context) {
		context.register(
			FALSE,
			ConfigPredicate.not(ConfigPredicate.alwaysTrue())
		);
		context.register(
			HAS_WILDER_WILD,
			ConfigPredicate.modLoaded(FrozenLibConstants.WILDER_WILD_MOD_ID)
		);
		context.register(
			HAS_TRAILIER_TALES,
			ConfigPredicate.modLoaded(FrozenLibConstants.TRAILIER_TALES_MOD_ID)
		);
		context.register(
			HAS_THE_COPPERIER_AGE,
			ConfigPredicate.modLoaded(FrozenLibConstants.THE_COPPERIER_AGE_MOD_ID)
		);
		context.register(
			HAS_CHAOS_HYPERCUBED,
			ConfigPredicate.modLoaded(FrozenLibConstants.CHAOS_HYPERCUBED_MOD_ID)
		);
		context.register(
			HAS_NETHERIER_NETHER,
			ConfigPredicate.modLoaded(FrozenLibConstants.NETHERIER_NETHER_MOD_ID)
		);
		context.register(
			HAS_SPRINGIER_LIFE,
			ConfigPredicate.modLoaded(FrozenLibConstants.SPRINGIER_LIFE_MOD_ID)
		);
		context.register(
			HAS_SIMPLE_COPPER_PIPES,
			ConfigPredicate.modLoaded(FrozenLibConstants.SIMPLE_COPPER_PIPES_MOD_ID)
		);
		context.register(
			HAS_FREEZE_FRAME,
			ConfigPredicate.modLoaded(FrozenLibConstants.FREEZE_FRAME_MOD_ID)
		);
		context.register(
			HAS_GLOWTONE,
			ConfigPredicate.modLoaded(FrozenLibConstants.GLOWTONE_MOD_ID)
		);
	}

	private static ResourceKey<ConfigPredicate> create(String name) {
		return ResourceKey.create(FrozenLibRegistries.CONFIG_PREDICATE_PROVIDER, FrozenLibConstants.id(name));
	}

	private FrozenLibConfigPredicates() {}
}
