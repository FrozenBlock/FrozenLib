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

package net.frozenblock.lib.debug.client.gui;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.client.ClientRegistryHelper;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
@ClientOnly
public final class FrozenLibDebugScreenEntries {
	private static final DeferredRegister<DebugScreenEntry> REGISTER = ClientRegistryHelper.createDeferredRegister(
		ClientRegistries.DEBUG_SCREEN_ENTRY,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<DebugScreenEntry, DebugEntryStructureStatuses> STRUCTURE_STATUSES = REGISTER.register(
		"structure_status",
		() -> new DebugEntryStructureStatuses()
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
