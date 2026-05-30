/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.resource_pack.impl.client;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;

/**
 * An extension of {@link FolderRepositorySource}.
 * <p>
 * Resource packs loaded from this repository will have a translatable name of frozenlib.resourcepack.[pack name without ".zip" extension].
 * <p>
 * Resource packs loaded from this repository will be marked as built-in and will always be enabled, with no way to disable them.
 */
@Environment(EnvType.CLIENT)
public class FrozenLibFolderRepositorySource extends FolderRepositorySource {
	private final String suffix;

	public FrozenLibFolderRepositorySource(Path path, PackType type, PackSource source, DirectoryValidator validator, String suffix) {
		super(path, type, source, validator);
		this.suffix = suffix;
	}

	public String getSuffix() {
		return this.suffix;
	}

}
