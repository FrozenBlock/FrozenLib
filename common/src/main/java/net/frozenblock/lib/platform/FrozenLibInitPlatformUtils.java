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

package net.frozenblock.lib.platform;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.service.DataAttachmentHelper;
import net.frozenblock.lib.platform.service.EventHelper;
import net.frozenblock.lib.platform.service.RegistryHelper;
import static net.frozenblock.lib.platform.PlatformUtil.load;

@UtilityClass
public class FrozenLibInitPlatformUtils {
	public static final RegistryHelper REGISTRY = load(RegistryHelper.class);
	public static final DataAttachmentHelper DATA_ATTACHMENT = load(DataAttachmentHelper.class);
	public static final EventHelper EVENT = load(EventHelper.class);
}
