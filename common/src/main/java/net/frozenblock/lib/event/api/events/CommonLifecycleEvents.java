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

package net.frozenblock.lib.event.api.events;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.RegistryAccess;

@UtilityClass
public final class CommonLifecycleEvents {
	/**
	 * Called when tags are loaded or updated. Implemented separately on Fabric and NeoForge.
	 * <p>
	 * Fabric: redirects to Fabric's {@code CommonLifecycleEvents}.
	 * <p>
	 * NeoForge: client invocations are passed through NeoForge's {@code TagsUpdatedEvent.ClientPacketReceived},
	 * while server invocations are implemented via mixin to maintain parity with Fabric.
	 */
	public static final Event<TagsLoaded> TAGS_LOADED = EventRegistry.createEnvironmentEvent(TagsLoaded.class,
		callbacks -> (registries, client) -> {
		for (TagsLoaded callback : callbacks) callback.onTagsLoaded(registries, client);
	});

	@FunctionalInterface
	public interface TagsLoaded {
		/**
		 * @param registries Up-to-date registries from which the tags can be retrieved.
		 * @param client True if the client just received a sync packet, false if the server just (re)loaded the tags.
		 */
		void onTagsLoaded(RegistryAccess registries, boolean client);
	}
}
