/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.item.mixin.sherd;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.item.impl.sherd.DecoratedPotPatternRegistryEntrypoint;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;

@Mixin(value = DecoratedPotPatterns.class, priority = 50)
public class DecoratedPotPatternsMixin {

	@Shadow
	public static Map<Item, ResourceKey<DecoratedPotPattern>> ITEM_TO_POT_TEXTURE;

	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void frozenLib$createMap(CallbackInfo info) {
		Map<Item, ResourceKey<DecoratedPotPattern>> mutableSherdMap = new Object2ObjectOpenHashMap<>(ITEM_TO_POT_TEXTURE);
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:decorated_pot_patterns", DecoratedPotPatternRegistryEntrypoint.class).forEach(entrypoint -> {
			DecoratedPotPatternRegistryEntrypoint decoratedPotPatternRegistryEntrypoint = entrypoint.getEntrypoint();
			decoratedPotPatternRegistryEntrypoint.registerForItems(mutableSherdMap);
		});
		ITEM_TO_POT_TEXTURE = ImmutableMap.copyOf(mutableSherdMap);
	}

	@Inject(
		method = "bootstrap",
		at = @At(
			value = "RETURN",
			shift = At.Shift.BEFORE
		)
	)
	private static void frozenLib$bootstrap(Registry<DecoratedPotPattern> registry, CallbackInfoReturnable<DecoratedPotPattern> info) {
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:decorated_pot_patterns", DecoratedPotPatternRegistryEntrypoint.class).forEach(entrypoint -> {
			try {
				DecoratedPotPatternRegistryEntrypoint decoratedPotPatternRegistryEntrypoint = entrypoint.getEntrypoint();
				decoratedPotPatternRegistryEntrypoint.bootstrap(registry);
			} catch (Throwable ignored) {}
		});
	}
}
