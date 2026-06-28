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

package net.frozenblock.lib;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.tag.api.TagKeyArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;

public final class FrozenLibMain {

	public static void preQuiltInit() {
		FireData.init();
	}

	public static void quiltInit() {}

	public static void init() {
		var register = FrozenLibInitPlatformUtils.REGISTRY.createDeferredRegister(
			Registries.COMMAND_ARGUMENT_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register(
			"tag_key",
			() -> new TagKeyArgument.Info<>(),
			info -> ArgumentTypeInfos.BY_CLASS.put(
				ArgumentTypeInfos.fixClassType(TagKeyArgument.class),
				info
			)
		);

		register.register();
	}
}
