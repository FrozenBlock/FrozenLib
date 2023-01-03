/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.resource.loader.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
//import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

/**
 * Events related to the resource loader.
 * <p>
 * Modified to work on Fabric
 */
public final class ResourceLoaderEvents {
    private ResourceLoaderEvents() {
        throw new UnsupportedOperationException("ResourceLoaderEvents contains only static definitions.");
    }

    /**
     * An event indicating the start of the reloading of data packs on a Minecraft server.
     * <p>
     * This event should not be used to load resources.*//*, use {@link ResourceLoader#registerReloader(IdentifiableResourceReloader)} instead.
     */
    public static final Event<StartDataPackReload> START_DATA_PACK_RELOAD = FrozenEvents.createEnvironmentEvent(StartDataPackReload.class,
            callbacks -> (server, resourceManager) -> {
                for (var callback : callbacks) {
                    callback.onStartDataPackReload(server, resourceManager);
                }
            });

    /**
     * An event indicating the end of the reloading of data packs on a Minecraft server.
     * <p>
     * This event should not be used to load resources.*//*, use {@link ResourceLoader#registerReloader(IdentifiableResourceReloader)} instead.
     */
    public static final Event<EndDataPackReload> END_DATA_PACK_RELOAD = FrozenEvents.createEnvironmentEvent(EndDataPackReload.class,
            callbacks -> (server, resourceManager, error) -> {
                for (var callback : callbacks) {
                    callback.onEndDataPackReload(server, resourceManager, error);
                }
            });

    /**
     * Functional interface to be implemented on callbacks for {@link #START_DATA_PACK_RELOAD}.
     *
     * @see #START_DATA_PACK_RELOAD
     */
    @FunctionalInterface
    public interface StartDataPackReload extends CommonEventEntrypoint {
        /**
         * Called before data packs on a Minecraft server have been reloaded.
         *
         * @param server             the server, may be {@code null} for the first reload
         * @param oldResourceManager the old resource manager, to be replaced, may be {@code null} for the first reload
         */
        void onStartDataPackReload(@Nullable MinecraftServer server, @Nullable ResourceManager oldResourceManager);
    }

    /**
     * Functional interface to be implemented on callbacks for {@link #END_DATA_PACK_RELOAD}.
     *
     * @see #END_DATA_PACK_RELOAD
     */
    @FunctionalInterface
    public interface EndDataPackReload extends CommonEventEntrypoint {
        /**
         * Called after data packs on a Minecraft server have been reloaded.
         * <p>
         * If the reload was not successful, the old data packs will be kept.
         *
         * @param server          the server, may be {@code null} for the first reload
         * @param resourceManager the resource manager, may be {@code null} if the data pack reload failed
         * @param error           present if the data pack reload failed, or {@code null} otherwise
         */
        void onEndDataPackReload(@Nullable MinecraftServer server, ResourceManager resourceManager, @Nullable Throwable error);
    }
}
