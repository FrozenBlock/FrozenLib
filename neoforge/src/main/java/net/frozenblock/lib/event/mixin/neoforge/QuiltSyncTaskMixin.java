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

package net.frozenblock.lib.event.mixin.neoforge;

import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.QuiltSyncTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(QuiltSyncTask.class)
@org.spongepowered.asm.mixin.Implements(
	@org.spongepowered.asm.mixin.Interface(
		iface = ICustomConfigurationTask.class,
		prefix = "frozenLib$"
	)
)
public class QuiltSyncTaskMixin {

	@Unique
	public void frozenLib$run(Consumer<CustomPacketPayload> send) {
		QuiltSyncTask self = (QuiltSyncTask) (Object) this;
		self.start(pkt -> {});
	}
}
