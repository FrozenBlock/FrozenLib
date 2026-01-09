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

package net.frozenblock.lib.file.transfer;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FileTransferRebuilder {
	private static final Object2ObjectLinkedOpenHashMap<Path, ArrayList<FileTransferPacket.FileTransferSnippet>> SERVER_PENDING_RECEIVED_TRANSFERS = new Object2ObjectLinkedOpenHashMap<>();
	private static final Object2ObjectLinkedOpenHashMap<Path, ArrayList<FileTransferPacket.FileTransferSnippet>> CLIENT_PENDING_RECEIVED_TRANSFERS = new Object2ObjectLinkedOpenHashMap<>();

	public static boolean onReceiveFileTransferPacket(Path destinationPath, FileTransferPacket.FileTransferSnippet snippet, int totalPacketCount, boolean client) throws IOException {
		Object2ObjectLinkedOpenHashMap<Path, ArrayList<FileTransferPacket.FileTransferSnippet>> map = client ? CLIENT_PENDING_RECEIVED_TRANSFERS : SERVER_PENDING_RECEIVED_TRANSFERS;

		try {
			if (map.containsKey(destinationPath)) {
				final ArrayList<FileTransferPacket.FileTransferSnippet> snippets = map.get(destinationPath);
				snippets.add(snippet);

				if (snippet.index() == totalPacketCount) {
					final List<Byte> finalBytes = new ArrayList<>();
					snippets.forEach(snippetInList -> finalBytes.addAll(List.of(ArrayUtils.toObject(snippetInList.bytes()))));

					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(ArrayUtils.toPrimitive(finalBytes.toArray(new Byte[0]))), destinationPath.toFile());
					map.remove(destinationPath);
					return true;
				}
			} else {
				if (snippet.index() == totalPacketCount) {
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(snippet.bytes()), destinationPath.toFile());
					return true;
				} else {
					ArrayList<FileTransferPacket.FileTransferSnippet> snippets = new ArrayList<>();
					snippets.add(snippet);
					map.put(destinationPath, snippets);
				}
			}
		} catch (Exception e) {
			map.remove(destinationPath);
			throw e;
		}

		return false;
	}

}
