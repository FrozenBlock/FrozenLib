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

package net.frozenblock.lib.commands;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.tag.api.TagArgument;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibSerializableArgumentTypes {

	public static void init() {
		final DeferredRegister<SerializableArgument.Type<?>> register = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, FrozenLibConstants.MOD_ID);

		register.register("tag", () -> TagArgument.TYPE);

		register.register();
	}
}
