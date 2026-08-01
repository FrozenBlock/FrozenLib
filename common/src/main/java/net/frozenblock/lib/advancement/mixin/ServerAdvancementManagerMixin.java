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

package net.frozenblock.lib.advancement.mixin;

import java.util.Map;
import net.frozenblock.lib.advancement.api.AdvancementEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerAdvancementManager.class, priority = 1500)
public class ServerAdvancementManagerMixin {

	@Final
	@Shadow
	private Map<Identifier, AdvancementHolder> advancements;

	@Inject(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;values()Ljava/util/Collection;"
		)
	)
	private void modifyAdvancement(HolderLookup.Provider registries, CallbackInfo ci) {
		for (AdvancementHolder holder : advancements.values()) AdvancementEvents.INIT.invoker().onInit(holder, registries);
	}
}
