package net.frozenblock.lib.resource_pack.impl;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import java.nio.file.Path;

public class FrozenLibFolderRepositorySource extends FolderRepositorySource {

	public FrozenLibFolderRepositorySource(Path path, PackType packType, PackSource packSource, DirectoryValidator directoryValidator) {
		super(path, packType, packSource, directoryValidator);
	}

}
